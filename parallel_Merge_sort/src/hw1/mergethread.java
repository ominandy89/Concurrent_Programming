package hw1;


import hw1.MergeSortTest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



public class mergethread implements Runnable {
    final long[] input;
    final int start ;
    int end;
    final long[] scratch;
    final int arg;
    public mergethread(final long[] input, final int start,int end, final long scratch[], final int arg){
                       this.input=input;
                       this.start=start;
                       this.end=end;
                       this.scratch=scratch;
                       this.arg=arg;
                       
                      }
    public void run(){
//        System.out.println(" inside child thread" + start);
    MergeSortTest.parallelMergeSort(input, start, end, scratch, arg);
}
}
