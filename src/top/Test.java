package top;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
	private static final int PARALLELISM = 10;
	private static final int DOMAINS_COUNT = 10000000;
	private static final int ADD_COUNT = 10000000;
	private static final int ADD_BLOCK = 100000;
	
	private static ExecutorService executor = new ThreadPoolExecutor(PARALLELISM, PARALLELISM,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(ADD_BLOCK));
	private static List<String> domains;
	
	private static SynchronizedTop<String> top = new SynchronizedTop<>();
//	private static SynchronizedArrayTop<String> top = new SynchronizedArrayTop<>();
	
//	private static AggregatedTop<String> top = new AggregatedTop<>(IntStream.range(0, 100)
//			.mapToObj(i -> new SynchronizedTop<String>()).collect(Collectors.toList()));
	
	private static AtomicInteger counter = new AtomicInteger();
	
	public static void main(String [] args) throws InterruptedException {
		domains = generateDomains(DOMAINS_COUNT);
		System.out.println("Domains are generated");
		
		long t = System.currentTimeMillis();

		for (int i = 0; i < ADD_COUNT/ADD_BLOCK; ++i) {
			List<Future<?>> futures = new ArrayList<>();
			for (int j = 0; j < ADD_BLOCK; ++j) {
				futures.add(executor.submit(() -> call()));
			}
			for (int j = 0; j < ADD_BLOCK; ++j) {
				try {
					futures.get(j).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
		long elapsed = System.currentTimeMillis() - t;
		System.out.println(String.format("Adds took: %sms (%s per second)", elapsed, ADD_COUNT/(elapsed/1000)));
		t = System.currentTimeMillis();
		System.out.println("Top 1000: " + top.get(1000));
		System.out.println("Top count took: " + (System.currentTimeMillis() - t) + "ms");
	}
	
	private static void call() {
		try {
			int index = ThreadLocalRandom.current().nextInt(domains.size());
			top.add(domains.get(index));
			if (index % 10000 == 0) {
				executor.submit(() -> {
					System.out.println("Top 10: " + top.get(10));
				});
			}
			
			int c = counter.incrementAndGet();
			if (c % ADD_BLOCK == 0) {
				System.out.println(c + " completed");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static List<String> generateDomains(int n) {
		Set<String> set = new HashSet<>();
		Random r = new Random();
		while (set.size() < n) {
			byte [] bytes = new byte[10];
			r.nextBytes(bytes);
			for (int i = 0; i < bytes.length; ++i) {
				bytes[i] = (byte)('A' + Math.abs(bytes[i] % 20));
			}
			set.add(new String(bytes));
		}
		
		return new ArrayList<>(set);
	}
}
