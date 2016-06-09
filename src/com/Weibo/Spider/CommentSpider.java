package com.Weibo.Spider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.Weibo.Beans.CommentInfo;
import com.Weibo.Fetcher.CommentFetcher;
import com.Weibo.Queues.CommentLinkQueue;
import com.Weibo.Services.CommentHandler;

public class CommentSpider implements Runnable {
	public List<CommentInfo> infoList = new LinkedList<>();
	public CommentFetcher fetcher = new CommentFetcher();
	public CommentHandler handler = new CommentHandler();
	private CountDownLatch latch;
	private int retryTimes = 5;
	private boolean result = false;

	public CommentSpider(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void run() {
		String newLink = null;
		while ((newLink = CommentLinkQueue.take()) != null) {// 当队列不为空时
			System.out.println(Thread.currentThread().getName() + ":   " + "current link :   " + newLink+"   Queue size:   "+CommentLinkQueue.size());
			String jsonContent = fetcher.fetch(newLink);// 发出第一个请求
			while (jsonContent == null && retryTimes-->=0) {//若获取json数据失败，等待2秒，重复尝试获取3次
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}
				jsonContent = fetcher.fetch(newLink);
			}
			retryTimes = 3;
			
			while (jsonContent != null && retryTimes-- >= 0) {
				result = handler.handle(jsonContent);
				if (result) {
					break;
				} else {// 若获取数据失败，睡眠一段时间，重新发起请求
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
					}
					jsonContent = fetcher.fetch(newLink);
				}
			}
			if (result == false){
				System.err.println(Thread.currentThread().getName() + ":    failed to fetch data from link :   " + newLink);
			}
			result = false;
			retryTimes = 3;
			System.out.println(Thread.currentThread().getName() + ":   finished!");
			try {
				Thread.sleep(2000);// 防止请求发送过快
			} catch (InterruptedException e) {
				break;
			}
		}
		latch.countDown();
	}
}
