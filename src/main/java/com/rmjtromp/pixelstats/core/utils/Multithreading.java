package com.rmjtromp.pixelstats.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {
	
	private Multithreading() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final AtomicInteger counter = new AtomicInteger(0);

	private static final ScheduledExecutorService RUNNABLE_POOL;

	public static ThreadPoolExecutor POOL;

	static {
		RUNNABLE_POOL = Executors.newScheduledThreadPool(10, r -> new Thread(r, "PixelStats Thread " + counter.incrementAndGet()));
		POOL = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, String.format("Thread %s", new Object[] { Integer.valueOf(counter.incrementAndGet()) })));
	}

	public static ScheduledFuture<?> schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
		return RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
	}

	public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
		return RUNNABLE_POOL.schedule(r, delay, unit);
	}

	public static void runAsync(Runnable runnable) {
		POOL.execute(runnable);
	}

	public static Future<?> submit(Runnable runnable) {
		return POOL.submit(runnable);
	}
}