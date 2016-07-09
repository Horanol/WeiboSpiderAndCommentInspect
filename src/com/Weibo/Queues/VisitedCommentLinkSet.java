package com.Weibo.Queues;

import java.util.HashSet;
import java.util.Set;

public class VistedCommentLinkSet {
	public static Set<String> vistedCommentLinkSet = new HashSet<>();
	public static synchronized boolean contains(String o) {
		return vistedCommentLinkSet.contains(o);
	}
	public static synchronized int size() {
		return vistedCommentLinkSet.size();
	}
	public static synchronized void add(String o) {
		if (o != null) {
			vistedCommentLinkSet.add(o);
		}
	}
	public static synchronized void remove(String o) {
		vistedCommentLinkSet.remove(o);
	}
}
