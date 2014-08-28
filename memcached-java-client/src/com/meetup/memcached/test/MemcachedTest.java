/**
 * Copyright (c) 2008 Greg Whalin
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the BSD license
 *
 * This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * You should have received a copy of the BSD License along with this
 * library.
 *
 * @author Greg Whalin <greg@meetup.com> 
 */
package com.meetup.memcached.test;

import java.util.Random;

import com.meetup.memcached.*;
import com.meetup.memcaheded.db.DatabaseCon;
import org.apache.log4j.PropertyConfigurator;

public class MemcachedTest {
	public static void main(String[] args) {
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "/bin/log4j.properties");// 加载.properties文件
		String[] serverlist = { "192.168.3.224:20000", "192.168.3.204:20000",
				"192.168.3.244:20000", "192.168.3.218:20000" };

		// initialize the pool for memcache servers
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(serverlist);

		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(50);
		pool.setMaintSleep(10);

		pool.setNagle(false);
		pool.initialize();

		int threads = Integer.parseInt(args[0]);
		int runs = Integer.parseInt(args[1]);
		int Nums = Integer.parseInt(args[2]); // how many kilobytes
		int size = Integer.parseInt(args[3]); // how many kilobytes
		double rate = Double.parseDouble(args[4]); // 读写比例

		// get object to store
		byte[] obj = new byte[size];
		for (int i = 0; i < size; i++) {
			obj[i] = '1';
		}
		String value = new String(obj);
		String[] keys = new String[Nums];
		for (int i = 0; i < Nums; i++) {
			keys[i] = "" + i;
		}

		DatabaseCon.getInstance().start();
//		for (int i = 0; i < 100; i++) {
//			DatabaseCon.getInstance().setKey(String.valueOf(i), value);
//		}
//		System.out.println("insert data successful");

		for (int i = 0; i < threads; i++) {
			bench b = new bench(runs, Nums, i, value, keys, rate);
			b.start();
		}
		// pool.shutDown();
		// System.exit(1);
		
		MemcachedClient mc = new MemcachedClient();
		System.out.println("stats.size = " + mc.stats(serverlist).size());
	}

	/**
	 * Test code per thread.
	 */
	private static class bench extends Thread {
		private int runs;
		private String object;
		private String[] keys;
		private int nums;
		@SuppressWarnings("unused")
		private int ticks = 0;
		@SuppressWarnings("unused")
		private long diffTime = 0;
		private double rate;

		public bench(int runs, int nums, int threadNum, String object,
				String[] keys, double rate) {
			this.runs = runs;
			this.object = object;
			this.keys = keys;
			this.nums = nums;
			this.rate = rate;
		}

		public void run() {
			MemcachedClient mc = new MemcachedClient();
			mc.setCompressEnable(false);
			mc.setCompressThreshold(0);
			//DatabaseCon.getInstance().start();
			long time = 0;
			time = System.nanoTime();
			randReadWrite(mc, rate);
			time = System.nanoTime() - time;
			System.out.println(time / 1000000000.0);
		}

		public void randReadWrite(MemcachedClient mc, double scale) {
			final Random randNum = new Random();
			for (int i = 1; i <= runs; i++) {
				if (Math.random() < scale) {
					String keyword = keys[randNum.nextInt(nums)];
					Object out = mc.get(keyword);
					if (out == null){
						String value = DatabaseCon.getInstance().queryKey(keyword);
						if (value == null){
							value = object;
						}
						mc.set(keyword, value);
					}
				} else {
					mc.set(keys[randNum.nextInt(nums)], object);
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							DatabaseCon.getInstance().setKey(keys[randNum.nextInt(nums)], object);
//						}
//					}).start();
				}

				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

//		public synchronized static void method() {
//			if (ticks == 0) {
//				diffTime = System.currentTimeMillis();
//			}
//			ticks++;
//			if (ticks == 5000) {
//				System.out.println(5000.0 * 1000 / (System.currentTimeMillis() - diffTime));
//				ticks = 0;
//			}
//		}
	}
}
