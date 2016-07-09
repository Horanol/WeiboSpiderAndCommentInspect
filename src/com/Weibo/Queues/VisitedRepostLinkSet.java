package com.Weibo.Queues;

import java.util.HashSet;
import java.util.Set;

public class VisitedRepostLinkSet {
	public static Set<String> visitedRepostLinkSet = new HashSet<>();
	public static synchronized boolean contains(String o) {
		return visitedRepostLinkSet.contains(o);
	}
	public static synchronized int size() {
		return visitedRepostLinkSet.size();
	}
	public static synchronized void add(String o) {
		if (o != null) {
			visitedRepostLinkSet.add(o);
		}
	}
	public static synchronized void remove(String o) {
		visitedRepostLinkSet.remove(o);
	}
}
