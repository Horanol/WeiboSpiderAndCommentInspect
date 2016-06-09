package com.Weibo.Spider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.Weibo.Beans.RepostInfo;
import com.Weibo.Fetcher.RepostFetcher;
import com.Weibo.Queues.RepostLinkQueue;
import com.Weibo.Services.RepostHandler;

public class RepostSpider implements Runnable {
	public List<RepostInfo> infoList = new LinkedList<>();
	public RepostFetcher fetcher = new RepostFetcher();
	public RepostHandler handler = new RepostHandler();
	private CountDownLatch latch;
	
	public RepostSpider(CountDownLatch latch) {
		this.latch = latch;
	}
	
	/**
	 * 从队列中取出地址，当等待时间超时则说明队列已空，线程退出
	 */
	@Override
	public void run() {
		String newLink = null;
		while ((newLink = RepostLinkQueue.take())!=null) {//当队列不为空时
			String jsonContent = fetcher.fetch(newLink);//发出第一个请求
			if (jsonContent != null) {
				handler.handle(jsonContent);
			}
			try {
				Thread.sleep(1500);//防止请求发送过快
			} catch (InterruptedException e) {
				break;
			}
		}
		latch.countDown();
	}


}
