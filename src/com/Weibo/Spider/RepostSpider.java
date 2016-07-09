package com.Weibo.Spider;

import java.util.LinkedList;
import java.util.List;
import com.Weibo.Beans.RepostInfo;
import com.Weibo.Exceptions.EmptyPageException;
import com.Weibo.Exceptions.LogicException;
import com.Weibo.Exceptions.WebException;
import com.Weibo.Fetcher.RepostFetcher;
import com.Weibo.Queues.RepostLinkQueue;
import com.Weibo.Queues.VisitedRepostLinkSet;
import com.Weibo.Services.RepostHandler;

public class RepostSpider implements Runnable {
	public List<RepostInfo> infoList = new LinkedList<>();
	public RepostFetcher fetcher = new RepostFetcher();
	public RepostHandler handler = new RepostHandler();
	private int retryTimes = 6;

	/**
	 * 从队列中取出地址，当等待时间超时则说明队列已空，线程退出
	 */
	@Override
	public void run() {
		String newLink = null;
		while ((newLink = RepostLinkQueue.take()) != null) {// 当队列不为空时
			if (VisitedRepostLinkSet.contains(newLink)) {
				continue;
			}
			System.out.println(Thread.currentThread().getName()+" : RepostQueue.size() = " + RepostLinkQueue.size()+" current link: "+newLink);
			while (retryTimes-- >= 0) {
				String jsonContent = null;
				try {
					jsonContent = fetcher.fetch(newLink);// 发出第一个请求
					handler.handle(jsonContent);
				} catch (LogicException e) {
					if (e instanceof EmptyPageException) {
						break;
					}
				} catch (WebException e) {
				}
				sleepAWhile(2000);
			}
			if (retryTimes < 0) {
				System.err.println(
						Thread.currentThread().getName() + ":    failed to fetch data from link :   " + newLink);
			} else {
				System.out.println(Thread.currentThread().getName() + ":   finished!");
			}
			VisitedRepostLinkSet.add(newLink);
			sleepAWhile(1500);// 防止请求发送过快
			retryTimes = 5;
		}

	}

	private void sleepAWhile(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
		}
	}
}
