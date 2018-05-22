/*
Name: Nicholas Keen
Course: CIS 495.002 Bio-Inspired A.I.
Fall 2017
Assignment 02: Genetic Algorithm
*/

import java.util.*;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

//Splits the numbers 1-10 into two groups such that one group's sum is 36
//and the other group's product is 360, terminating if after 1000 tournaments
//no solution is found. The algorithm repeats ten times.
public class GeneticAlgorithm{
  public static void main(String[] args){
    //Initialize important variables.
    String result = "Results: \n";
    final int SUMTARGET = 36;
    final int PRODTARGET = 360;
    int sum = 0;
    int prod = 1;
    //Repeat algorithm ten times.
    for(int i = 0; i < 10; i++){
      int[][] pop = popGenerator();
      result = tournaments(sum, prod, SUMTARGET, PRODTARGET, pop, result);
    }
    resultFile(result);
  }
  
  //Outputs the result to a file named "GeneticAlgorithm.txt"
  //Parameter: String s - the result.
  public static void resultFile(String s){
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream("GeneticAlgorithm.txt"), "utf-8"))) {
    writer.write(s);
    }catch(IOException e){
    }
  }
  
  //performs the tournaments 1,000 times.
  //Parameters: int sum - the intitial sum.
  //            int prod - the initial product.
  //            int SUMTARGET - the target value for the sum.
  //            int PRODTARGET - the target value for the product.
  //            int[][] pop - an array of 'genomes'
  //            String result - the string that will eventually be written
  //                            to the output file.
  //Returns: String - the string that will eventually be written 
  //                  to the output file.
  public static String tournaments(int sum, int prod, int SUMTARGET,
                                 int PRODTARGET, int[][] pop, String result){
    //Do 1,000 tournaments.
    for(int j = 0; j < 1000; j++){
      String s = "";
      //Pick two random 'genome' arrays from the population.
      int n = randNum(30);
      int m = randNum(30);
      int[] a = pop[n];
      int[] b = pop[m];
      //Evaluate their fitness.
      double erra = fitnessFunction(a, sum, prod, SUMTARGET, PRODTARGET);
      double errb = fitnessFunction(b, sum, prod, SUMTARGET, PRODTARGET);
      if(erra <= 0.01){
        s = output(a, n, j);
        System.out.println(s);
        result = result+"\n"+s;
        break;
      }
      if(errb <= 0.01){
        s = output(b, m, j);
        System.out.println(s);
        result = result+"\n"+s;
        break;
      }
      //Choose a winner.
      pickWinner(a, b, erra, errb);
      if(j == 999){
        s = "No solution was found!\n";
        System.out.println(s);
        result = result+"\n"+s;
      }
    }
    return result;
  }
  
  //Chooses a winner based on fitness evaluation.
  //Parameters: int[] a - genome a.
  //            int[] b - genome b.
  //            double erra - fitness evaluation for a.
  //            double errb - fitness evaluation for b.
  public static void pickWinner(int[] a, int[] b, double erra, double errb){
    //a won.
    if(erra < errb)
      recombAndMutate(a, b);
    //b won.
    else if(errb > erra)
      recombAndMutate(b, a);
    //Tie! choose a or b randomly.
    else{
      if(randNum(2) == 0)
        recombAndMutate(a, b);
      else
        recombAndMutate(b, a);
    }
  }
  
  //Produces an output string.
  //Parameters: int[] a - the champion genome.
  //            int n - a's position in the population.
  //            int j - the tournament number.
  //Returns: String - string to be outputted with results file.
  public static String output(int[] a, int n, int j){
    String s = "Number of Tournaments: "+j+"\nChampion Genome: "
                             +(n+1)+"\nChampion Phenotype: "+
                             Arrays.toString(a)+"\nSum: "+findSum(a)+
                             "\nProduct: "+findProd(a)+"\n";
    return s;
  }
  
  //Produces a random number between 0 and n.
  //Parameter: int n - a range value.
  //Returns: int - a random number between 0 and n.
  public static int randNum(int n){
    Random rand = new Random();
    return rand.nextInt(n);
  }
  
  //Recombines and Mutates the losing genome.
  //Parameters: int[] a - winning genome.
  //            int[] b - losing genome.
  public static void recombAndMutate(int[] a, int[] b){
    //recombines b with a's elements with a factor of 0.5.
    for(int i = 0; i < 10; i++){
      if(randNum(2) == 0)
        b[i] = a[i];
    }
    //mutates b's elements with a factor of 0.1.
    for(int i = 0; i < 10; i++){
      if(randNum(10) == 0){
        if(b[i] == 0)
          b[i] = 1;
        else
          b[i] = 0;
      }
    }
  }
  
  //Finds the sum of a genome.
  //Parameter: int[] a - genome a.
  //Returns: int - the sum genome a produces.
  public static int findSum(int[] a){
    int sum = 0;
    for(int i = 0; i < 10; i++){
      if(a[i] == 0)
        sum = sum + (i+1);
    }
    return sum;
  }
  
  //Finds the product of a genome.
  //Parameter: int[] a - genome a.
  //Returns: int - the product genome a produces.
  public static int findProd(int[] a){
    int prod = 1;
    for(int i = 0; i < 10; i++){
      if(a[i] == 1)
        prod = prod * (i+1);
    }
    return prod;
  }
  
  //Evaluates a genome's fitness.
  //Parameters: int[] a - a genome.
  //            int sum - the initial sum.
  //            int prod - the initial product.
  //            int SUMTARGET - the target value for the sum.
  //            int PRODTARGET - the target value for the product.
  //Returns: double - the fitness evaluation of the genome.
  public static double fitnessFunction(int[] a, int sum, int prod,
                                       int SUMTARGET, int PRODTARGET){
    //Find genome sum and product.
    sum = findSum(a);
    prod = findProd(a);
    //Find sum error.
    double scaledSumError = ((double)sum - 
                            (double)SUMTARGET)/(double)SUMTARGET;
    //Find product error.
    double scaledProdError = ((double)prod - 
                             (double)PRODTARGET)/(double)PRODTARGET;
    //Add their absolute values to find combined error.
    double combinedError = Math.abs(scaledSumError) + 
                           Math.abs(scaledProdError);
    return combinedError;
  }
  
  //Generates the population.
  //Returns: int[][] - the population.
  public static int[][] popGenerator(){
    //Generate random seed based on time.
    Random rand = new Random(System.currentTimeMillis());
    //Population size of 30.
    int[][] pop = new int[30][];
    //Populate array with genome arrays.
    for(int i = 0; i < 30; i++){
      int[] a = new int[10];
      for(int j = 0; j < 10; j++){
        //Randomly generate genome arrays.
        if(rand.nextInt() < 0)
          a[j] = 0;
        else
          a[j] = 1;
      }
      //Add genome array to the population.
      pop[i] = a;
    }
    return pop;
  }
  
}