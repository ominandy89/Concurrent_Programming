package hw3;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;


class Test implements Runnable{

	Map<Integer, String> hm;
	int size;
	int iter;
	Random r;
	
	public Test(Map<Integer, String> hm, int iter, int size) {
	
		this.size = size;
		this.iter = iter;
		this.hm = hm;
		r = new Random();
		
	}
		
	public void run(){
	for(int i=0; i < iter; i+=1){
		int n = r.nextInt(iter);
		String t = hm.get(n);
		int n2 = r.nextInt(iter);
		hm.put(n2,i+"x");
		
	//	for(Map.Entry<Integer,String> entry:hm.entrySet()){
	//		String value=entry.getValue();
	//		Integer key=entry.getKey();
	//	}
		
//		Set<Entry<Integer, String>> t2 = hm.entrySet();
//		for(Entry<Integer, String> entry: t2){
//			Integer map = entry.getKey();
//			String value = entry.getValue();
//		}
		 
		
	}
}
}

public class driver{	
	public static void main(String[] args) {

		int avg_cow;
		int avg_concu;
		int avg_collect;
		long cow[] = new long[10000];
		
		long startTime[] = new long[10000];
		long concu[] = new long[10000];
		long collect[] = new long[10000];
		
		Map<Integer, String> hm = new CopyOnWriteHashMap<Integer, String>();
		Map<Integer, String> hm1 = new ConcurrentHashMap<Integer, String>();
		Map<Integer, String> hm2 = Collections.synchronizedMap(new HashMap<Integer,String>());
		
		
		Thread[] thread;
		int numThreads = Runtime.getRuntime().availableProcessors();
		int size = 1000;
		
		
		for(int i=0;i<size;i++){
			hm.put(i,i+"x");
			hm1.put(i,i+"x");
			hm2.put(i,i+"x");
		} 
		
		
		thread = new Thread[numThreads];
		int m = 0;
		for(int k = 100; k <=1000; k+=100){
			for(int i=0;i<numThreads;i++){
                              
				thread[i] = new Thread(new Test(hm, k, size));
			}
		startTime[m] = System.currentTimeMillis();
		for (int i = 0; i < numThreads; i++) thread[i].start();
		
		for (int i = 0; i < numThreads; i++)
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
		
		cow[m] = (System.currentTimeMillis() - startTime[m]);
		m++;
		
	} 
		
		thread = new Thread[numThreads];
		m=0;
		for(int k = 100; k <=1000; k+=100){
			for(int i=0;i<numThreads;i++){
				thread[i] = new Thread(new Test(hm1, k,size));
			}
		startTime[m] = System.currentTimeMillis();
		for (int i = 0; i < numThreads; i++) thread[i].start();
		
		for (int i = 0; i < numThreads; i++)
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
		
		concu[m] = (System.currentTimeMillis() - startTime[m]);
		m++;		
	} 

		
		thread = new Thread[numThreads];
		m=0;
		for(int k = 100; k <=1000; k+=100){
			for(int i=0;i<numThreads;i++){
				thread[i] = new Thread(new Test(hm2, k,size));
			}
		startTime[m] = System.currentTimeMillis();
		for (int i = 0; i < numThreads; i++) thread[i].start();
		
		for (int i = 0; i < numThreads; i++)
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
		
		collect[m] = (System.currentTimeMillis() - startTime[m]);
		m++;		
	} 

		StringBuilder sb = new StringBuilder();
		OperatingSystemMXBean sysInfo = ManagementFactory
				.getOperatingSystemMXBean();
		sb.append("Architecture = ").append(sysInfo.getArch()).append('\n');
		sb.append("OS = ").append(sysInfo.getName()).append(" version ")
				.append(sysInfo.getVersion()).append('\n');
		sb.append("Number of available processors = ").append(numThreads).append('\n');
		
		System.out.println(sb);
		
		System.out.println("SIZE\tCOWMap\tConcMap\tSyncMap");
                double sumcow = 0,sumconcu=0,sumsync=0;
		for(int l = 0;l<m;l++){
                    sumcow += cow[l];
                    sumconcu+= concu[l];
                    sumsync+=collect[l];
			
		}
		System.out.println(size+"\t"+(sumcow/m)+"\t"+(sumconcu/m)+"\t"+(sumsync/m));
		
		
		
		
	} 
	
}