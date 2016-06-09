package com.Weibo.Queues;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.Weibo.Beans.CommentInfo;

public class CommentLinkQueue {
	public static BlockingQueue<String> commentLinkQueue = new LinkedBlockingQueue<>();
	public static void add(String e) {
		commentLinkQueue.offer(e);
	}
	public static String take() {
		try {
			return commentLinkQueue.poll(3,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		return null;
	}
	public static boolean isEmpty() {
		return commentLinkQueue.isEmpty();
	}
	public static int size() {
		return commentLinkQueue.size();
	}
	public static boolean contains(CommentInfo e) {
		return commentLinkQueue.contains(e);
	}
}
