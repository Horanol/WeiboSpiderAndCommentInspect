package com.Weibo.Spider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.Weibo.Beans.CommentInfo;
import com.Weibo.Exceptions.EmptyPageException;
import com.Weibo.Exceptions.LogicException;
import com.Weibo.Exceptions.WebException;
import com.Weibo.Fetcher.CommentFetcher;
import com.Weibo.Queues.CommentLinkQueue;
import com.Weibo.Queues.VisitedCommentLinkSet;
import com.Weibo.Services.CommentHandler;

public class CommentSpider implements Runnable {
	public List<CommentInfo> infoList = new LinkedList<>();
	public CommentFetcher fetcher = new CommentFetcher();
	public CommentHandler handler = new CommentHandler();
	private int retryTimes = 5;

	@Override
	public void run() {
		String newLink = null;
		while ((newLink = CommentLinkQueue.take()) != null) {// 当队列不为空时
			if (VisitedCommentLinkSet.contains(newLink)) {
				continue;
			}
			System.out.println(Thread.currentThread().getName()+":  CommentQueue.size() is "+CommentLinkQueue.size()
			+"  url is "+newLink);
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
			if (retryTimes<0) {
				System.err.println(
						Thread.currentThread().getName() + ":    failed to fetch data from link :   " + newLink);
			}else {
				System.out.println(Thread.currentThread().getName() + ":   finished!");
			}
			VisitedCommentLinkSet.add(newLink);
			sleepAWhile(2000);
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
