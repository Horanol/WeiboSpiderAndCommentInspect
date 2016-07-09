package com.Weibo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Weibo.Beans.RepostInfo;
import com.Weibo.Dao.DBHandler;
import com.Weibo.Fetcher.WebFetcher;
import com.Weibo.Queues.CommentLinkQueue;
import com.Weibo.Queues.RepostLinkQueue;
import com.Weibo.Spider.*;
import com.Weibo.Utils.HtmlParser;
import com.Weibo.Utils.WeiboParser;

public class MyFirstCrawler {
	public static void main(String[] args) throws Exception {
		CountDownLatch latch = new CountDownLatch(3);
//		crawlHomePage();
		crawlComment();
		
		Thread commentThread = new Thread(new CommentSpider(latch));
		commentThread.setName("spider1");
		commentThread.start();
		
		Thread commentThread2 = new Thread(new CommentSpider(latch));
		commentThread2.setName("spider2");
		commentThread2.start();
		
		Thread commentThread3 = new Thread(new CommentSpider(latch));
		commentThread3.setName("spider3");
		commentThread3.start();
		
//		Thread spiderThread = new Thread(new RepostSpider(latch));
//		spiderThread.setName("spider1");
//		spiderThread.start();
//		
//		Thread spiderThread2 = new Thread(new RepostSpider(latch));
//		spiderThread2.setName("spider2");
//		spiderThread2.start();
//		
//		Thread spiderThread3 = new Thread(new RepostSpider(latch));
//		spiderThread3.setName("spider3");
//		spiderThread3.start();
		
//		Thread spiderThread4 = new Thread(new RepostSpider(latch));
//		spiderThread4.setName("spider4");
//		spiderThread4.start();
		
		latch.await();
		// 测试用例
//		 FileReader reader = new FileReader("F://json.txt");
//		 BufferedReader bufferedReader = new BufferedReader(reader);
//		 String str= null;
//		 StringBuilder builder = new StringBuilder();
//		 while ((str=bufferedReader.readLine())!=null) {
//		 builder.append(str);
//		 }
//		 String body = builder.toString();
//		 Document document = JsonParser.getHtmlDocFromJSON(body);
//		 List<RepostInfo> infos = HtmlParser.parseContentToBeans(document);
//		 for (RepostInfo info : infos) {
//			System.out.println(info);
//		}
//		 bufferedReader.close();
        

//		DBHandler.storeXML();
		DBHandler.storeComments();
	}

	private static void crawlHomePage() {
		crawlRepost();
		crawlComment();
	}

	private static void crawlRepost() {
		//获取转发页面
		String url = "http://weibo.com/1259193624/Ck3nq1aWn?type=repost&filter=hot";
		Document document = WebFetcher.fetchHtml(url);
		int amount = HtmlParser.getAllRepostAmount(document);
		System.out.println("Total repost: "+amount);
		// 获取root-action-data
		String rootActionData = HtmlParser.getActionData(document);
		// 拼凑出ajax请求地址
		String ajaxUrl = HtmlParser.getRepostUrl(rootActionData);
		
		String owner = WeiboParser.getWeiboOwnerName(document);
		String publishTime = WeiboParser.getWeiboPublishTime(document);
		RepostInfo info = new RepostInfo(1,owner, amount, publishTime, "null", ajaxUrl);
		DBHandler.recordRepostInfo(info);
		RepostLinkQueue.add(ajaxUrl);
	}

	private static void crawlComment() {
		//获取评论页面
//		String url = "http://weibo.com/1259193624/Ck3nq1aWn?type=comment";
		String url = "http://weibo.com/1749964961/Dve7U1IXU?type=comment";
		Document document = WebFetcher.fetchHtml(url);
		// 获取root-action-data
		String rootActionData = HtmlParser.getActionData(document);
		//获取评论的ajax地址
		String ajaxUrl = HtmlParser.getCommentUrl(rootActionData);
		//添加地址到CommentLinkQueue中去
		CommentLinkQueue.add(ajaxUrl);
	}
}
