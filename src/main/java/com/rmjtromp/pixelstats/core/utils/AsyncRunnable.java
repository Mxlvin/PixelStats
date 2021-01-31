package com.rmjtromp.pixelstats.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class AsyncRunnable {

	private final List<Thread> threads = new ArrayList<>();
	private final List<Runnable> queue = new ArrayList<>();
	
	private final String name;
	private final int limit;
	private Timer timer = new Timer();
	
	public AsyncRunnable(String name, int limit) {
		this.name = name;
		this.limit = limit;
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				threads.removeAll(threads.stream().filter(thread -> !thread.isAlive() || thread.isInterrupted()).collect(Collectors.toList()));
			}
			
		}, 1000, 5000);
	}

	public void addToQueue(Runnable run) {
		queue.add(run);
		if(threads.size() < limit) {
			Thread thread = new Thread(() -> {
				try {
					while(!queue.isEmpty()) {
						runFirst();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				threads.remove(Thread.currentThread());
			}, name);
			threads.add(thread);
			thread.start();
		}
	}
	
	private void runFirst() {
		synchronized (queue) {
			if(!queue.isEmpty()) {
				Runnable runnable = null;
				try {
					runnable = queue.get(0);
					queue.remove(0);
				} catch(IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				try {
					if(runnable != null) runnable.run();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void clearQueue() {
		queue.clear();
		threads.forEach(Thread::interrupt);
		threads.clear();
	}
	
}
