package com.poixson.utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class xThreadPoolFactory {
	private xThreadPoolFactory() {}

	public static final String MAIN_POOL_NAME = xThreadPool.MAIN_POOL_NAME;

	// thread pool instances
	protected static final ConcurrentMap<String, xThreadPool> pools =
			new ConcurrentHashMap<String, xThreadPool>();

	protected static final xThreadPool mainPool = getMainPool();

	// task hang monitor
	protected static final HangMonitorThread hangMonitor =
			new HangMonitorThread();
	protected static volatile Thread hangMonitorThread = null;



	/**
	 * Get main thread queue
	 */
	public static xThreadPool getMainPool() {
		return get(
			(String)  null,
			(Integer) null
		);
	}
	/**
	 * Get thread queue by name
	 */
	public static xThreadPool get(final String name) {
		return get(
			name,
			(Integer) null
		);
	}
	/**
	 * Get thread queue by name or create with x threads
	 * @param name Thread queue name to get or create.
	 * @param size Number of threads which can be created for this queue.
	 */
	public static xThreadPool get(final String poolName, final Integer poolSize) {
		String name = poolName;
		if (Utils.isEmpty(name) || MAIN_POOL_NAME.equalsIgnoreCase(name)) {
			if (mainPool != null) {
				return mainPool;
			}
			name = MAIN_POOL_NAME;
		}
		final int size = (
			poolSize == null
			? 1
			: poolSize.intValue()
		);
		if (size == 0) {
			if (mainPool != null) {
				return mainPool;
			}
			name = MAIN_POOL_NAME;
		}
		if (size < 0) throw new IllegalArgumentException("Invalid pool size: "+Integer.toString(size));
		if (MAIN_POOL_NAME.equalsIgnoreCase(name)) {
			if (mainPool != null) {
				return mainPool;
			}
		}
		// use existing pool instance
		{
			final xThreadPool pool = pools.get(name);
			if (pool != null) {
				return pool;
			}
		}
		// new pool instance
		synchronized(pools) {
			if (pools.containsKey(name)) {
				return pools.get(name);
			}
			// start task hang monitor thread
			if (hangMonitorThread == null) {
				hangMonitorThread = new Thread(hangMonitor);
				hangMonitorThread.setDaemon(true);
				hangMonitorThread.start();
			}
			// create new pool instance
			final xThreadPool pool =
					new xThreadPool(
						name,
						size
					);
			pools.put(
				name,
				pool
			);
			return pool;
		}
	}



	public static String[] getPoolNames() {
		return pools.keySet()
				.toArray(new String[0]);
	}



	protected static class HangMonitorThread extends xRunnable {

		private final xClock clock = xClock.get(true);

		public HangMonitorThread() {
			super("xThreadPoolHangMonitor");
		}

		@Override
		public void run() {
			final long currentTime = this.clock.getCurrentTime();
			final Iterator<xThreadPool> it =
					pools.values().iterator();
			while (it.hasNext()) {
				final xThreadPool pool = it.next();
				pool.checkTaskTimeouts(currentTime);
				ThreadUtils.Sleep(1000L);
			}
		}

	}



	/**
	 * Stop all thread pools (except main)
	 */
	public static void ShutdownAll() {
//TODO:
//		if (!Thread.currentThread().equals(mainThread)) {
//		getMainPool().runNow(new Runnable() {
//			@Override
//			public void run() {
//				ShutdownAll();
//			}
//		});
//		return;
		// run in main thread pool
		getMainPool().runLater(
			new xRunnable("xThreadPool-Shutdown") {
				@Override
				public void run() {
					synchronized(pools) {
						// stop threads
						final Iterator<xThreadPool> it = pools.values().iterator();
						while (it.hasNext()) {
							final xThreadPool pool = it.next();
							pool.Stop();
						}
					}
				}
			}
		);
	}
//TODO:
/*
		// wait for threads to stop
		{
			final Iterator<xThreadPool> it = pools.values().iterator();
			while (it.hasNext()) {
				final xThreadPool pool = it.next();
				if (pool.isMainPool())
					continue;
				try {
					synchronized(pool) {
						pool.wait();
					}
				} catch (InterruptedException e) {
					xLog.getRoot()
						.trace(e);
				}
				it.remove();
			}
		}
//TODO: after all threads have stopped
pool.running.set(false);
	}
	public static void Exit() {
		try {
			getMainPool().runLater(
					new RemappedRunnable(
							xThreadPool.class,
							"ExitNow"
					)
			);
		} catch (Exception e) {
			xLog.getRoot().trace(e);
			ExitNow(1);
		}
	}
	public static void ExitNow() {
		ExitNow(0);
	}
	public static void ExitNow(final int status) {
		// display threads still running
		utilsThread.displayStillRunning();
		System.out.println();
		System.out.println();
		System.exit(status);
	}
*/



}