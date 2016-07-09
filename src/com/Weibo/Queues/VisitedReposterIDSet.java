package com.Weibo.Queues;

import java.util.HashSet;
import java.util.Set;

public class VisitedReposterIDSet {
	public static Set<Long> set = new HashSet<>();
	public static synchronized boolean contains(Long o) {
		return set.contains(o);
	}
	public static synchronized int size() {
		return set.size();
	}
	public static synchronized void add(Long o) {
		if (o != null) {
			set.add(o);
		}
	}
	public static synchronized void remove(Long o) {
		set.remove(o);
	}
}
