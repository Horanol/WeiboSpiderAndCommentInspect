package com.Weibo.Queues;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.Weibo.Beans.RepostInfo;

public class RepostLinkQueue {
	public static final int QUEUE_SIZE = 1000;
	public static BlockingQueue<String> repostLinkQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
	public static void add(String e) {
		repostLinkQueue.offer(e);
	}
	public static String take() {
		try {
			return repostLinkQueue.poll(3,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		return null;
	}
	public static boolean isEmpty() {
		return repostLinkQueue.isEmpty();
	}
	public static int size() {
		return repostLinkQueue.size();
	}
	public static boolean contains(RepostInfo e) {
		return repostLinkQueue.contains(e);
	}
}
