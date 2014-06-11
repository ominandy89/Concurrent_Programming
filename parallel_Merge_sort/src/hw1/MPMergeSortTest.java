/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hw1;

/**
 *
 * @author Omi
 */

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import static java.lang.System.out;
import static java.lang.System.err;

public class MPMergeSortTest {

	private static int SAMPLES;
	private static int POINTS_PER_RUN;
	private static int INITIAL_SIZE;
	private static int NPROCS;

	private final static long SEED = 353L;

	public static long[] fillRandom(int size, long seed) {
		long[] data = new long[size];
		COP5618Random r = new COP5618Random(seed);
		for (int i = 0; i < data.length; ++i) {
			data[i] = r.nextLong();
		}
		return data;
	}

	 public static boolean isSorted(final long[] data, final int start, final int end) {
		Thread t1 = new Thread();
                Thread t2=new Thread();
        final int mid= (start+end)/2;
        t1=new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = start + 1; i < mid+1; i++) {
                    if (data[i - 1] > data[i]) {
                        System.out.println("Error:  i=" + i + " start= " + start
                                + " end=" + end + " data[i-1]=" + data[i - 1]
                                + " data[i]=" + data[i]);
                        throw new IllegalArgumentException("error");
                    }
                }
                
                
                }
            
        });
        
        t2=new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = mid + 1; i < end; i++) {
                    if (data[i - 1] > data[i]) {
                        System.out.println("Error:  i=" + i + " start= " + start
                                + " end=" + end + " data[i-1]=" + data[i - 1]
                                + " data[i]=" + data[i]);
                        throw new IllegalArgumentException("error");
                    }
                }
                
                
                }
        });
        try {
        t1.start();
        t2.start();
        
        
            
            t1.join();
            t2.join();
            
            
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
	}

	public static void seqMergeSort(long[] input, int start, int end,
			long scratch[]) {
		int size = end - start;
		if (size < 2)
			return;
		int pivot = (end - start) / 2 + start;
		seqMergeSort(input, start, pivot, scratch);
		seqMergeSort(input, pivot, end, scratch);
		System.arraycopy(input, start, scratch, start, size);
		merge(scratch, input, start, pivot, end);
	}

	static void merge(long[] src, long[] dest, int start, int pivot, int end) {
		int l = start;
		int r = pivot;
		int i = start;
		while (l < pivot && r < end) {
			if (src[l] <= src[r])
				dest[i++] = src[l++];
			else
				dest[i++] = src[r++];
		}
		while (l < pivot) {
			dest[i++] = src[l++];
		}
		while (r < end) {
			dest[i++] = src[r++];
		}
	}

	public static void parallelMergeSort(final long[] input, final int start,
			int end, final long scratch[], final int arg) {
                     if (arg<2)
                        seqMergeSort(input, start, end, scratch);
                    else{
            		int size = end - start;
		if (size < 2)
			return;
		int pivot = (end - start) / 2 + start;
                Thread l=new Thread(new mergethread(input,start,pivot,scratch,arg/2)); 
                Thread r=new Thread(new mergethread(input,pivot,end,scratch,arg/2));
                l.start();
                r.start();
                try{
                    l.join();
                    r.join();
                }catch (InterruptedException e)
                {}
                System.arraycopy(input, start, scratch, start, size);
		merge(scratch, input, start, pivot, end);
	}
	}

	public static void main(String[] args) {
		if (args.length < 3){
			err.println("Not enougg arguments.  Need  INITIAL_SIZE POINTS_PER_RUN SAMPLES");
			return;
		}
		
		INITIAL_SIZE = Integer.parseInt(args[0]);
		POINTS_PER_RUN = Integer.parseInt(args[1]);
		SAMPLES = Integer.parseInt(args[2]); // number of times to run each test

		NPROCS = Runtime.getRuntime().availableProcessors();

		// initialize array containing sizes of arrays to sort. Double the size each time.
		int size[] = new int[POINTS_PER_RUN];
		for (int i = 0, s = INITIAL_SIZE; i < POINTS_PER_RUN; i++, s*=2) {
			size[i] = s;
		}

		// create arrays for holding measurements (nanosecs)
		long timedJavaSort[] = new long[POINTS_PER_RUN];
		long timedSeqSort[] = new long[POINTS_PER_RUN];
		long timedParallelSort[] = new long[POINTS_PER_RUN];

/* Repeat each test SAMPLES time and accumulate results. Divide by
 *    SAMPLES before printing to get average
 */

		// Use sorting routing from java.util.Arrays.sort
		out.print("java.util.Arrays.sort");
		for (int rep = 0; rep < SAMPLES; rep++) {
			for (int i = 0; i < POINTS_PER_RUN; i++) {
				long[] data = fillRandom(size[i], SEED);
				long startTime = System.nanoTime();
				Arrays.sort(data);
				timedJavaSort[i] += (System.nanoTime() - startTime);
				if (!isSorted(data, 0, size[i])){
					err.println("Error: java.util.Arrays.sort");
				}
				out.print('.');
			}
		}
		out.println();

		// Sequential merge sort
		out.print("sequential merge sort");
		for (int rep = 0; rep < SAMPLES; rep++) {
			for (int i = 0; i < POINTS_PER_RUN; i++) {
				long[] data = fillRandom(size[i], SEED);
				long startTime = System.nanoTime();
				seqMergeSort(data, 0, data.length, new long[data.length]);
				timedSeqSort[i] += (System.nanoTime() - startTime);
				if (!isSorted(data, 0, size[i])){
					err.println("Error: sequential merge sort");
				}
				out.print('.');
			}
		}
		out.println();

		// Parallel merge sort
		out.print("parallel merge sort");
		for (int rep = 0; rep < SAMPLES; rep++) {
			for (int i = 0; i < POINTS_PER_RUN; i++) {
				long[] data = fillRandom(size[i], SEED);
				long startTime = System.nanoTime();
				parallelMergeSort(data, 0, data.length, new long[data.length],NPROCS);
				timedParallelSort[i] += (System.nanoTime() - startTime);
				if (!isSorted(data, 0, size[i])){
					err.println("Error: parallel merge sort");
				}
				out.print('.');
			}
		}
		out.println();

		// output formatted to be easily copied into a spreadsheet
		StringBuilder sb = new StringBuilder();
		OperatingSystemMXBean sysInfo = ManagementFactory
				.getOperatingSystemMXBean();
		sb.append("Architecture = ").append(sysInfo.getArch()).append('\n');
		sb.append("OS = ").append(sysInfo.getName()).append(" version ")
				.append(sysInfo.getVersion()).append('\n');
		sb.append("Number of available processors = ").append(NPROCS).append('\n');
		sb.append("size \t Java sort \t seq merge sort \t parallel merge sort \n");
		//divide by SAMPLES to get average.  
		for (int i = 0; i < POINTS_PER_RUN; ++i) {
			sb.append(size[i]).append('\t').
			   append(timedJavaSort[i] / SAMPLES ).append('\t'). 
			   append(timedSeqSort[i] / SAMPLES ).append('\t').
			   append(timedParallelSort[i] / SAMPLES ).append('\n');
		}
		System.out.println(sb);

	}

}
