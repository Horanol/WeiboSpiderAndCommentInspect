package com.Weibo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;

import com.Weibo.Beans.RepostInfo;
import com.Weibo.Dao.CommentDAO;
import com.Weibo.Dao.RepostDAO;
import com.Weibo.Fetcher.WebFetcher;
import com.Weibo.Queues.CommentLinkQueue;
import com.Weibo.Queues.RepostLinkQueue;
import com.Weibo.Spider.*;
import com.Weibo.Utils.HtmlParser;
import com.Weibo.Utils.WeiboParser;

public class WeiboCrawler {
	public static void main(String[] args) throws Exception {
		String weiboUrl = "http://weibo.com/2803301701/DDEv83OT2";
		ExecutorService service = Executors.newCachedThreadPool();
		crawlComment(service, weiboUrl);
		crawlRepost(service, weiboUrl);
		service.shutdown();
		if (service.awaitTermination(30, TimeUnit.MINUTES)) {
			RepostDAO.store();
			CommentDAO.store();
		}
		System.err.println("Spider Finished ...");
//		EmotionAnalyser.analyse();
//		ShowRepostNet.show();
	}

	private static void crawlRepost(ExecutorService repostService, String weiboUrl) {
		// 获取转发页面
		String url = weiboUrl + "?type=repost&filter=hot";
		Document document = WebFetcher.fetchHtml(url);
		while (document == null) {
			document = WebFetcher.fetchHtml(url);
		}
		int amount = HtmlParser.getAllRepostAmount(document);
		System.out.println("Total repost: " + amount);
		// 获取root-action-data
		String rootActionData = HtmlParser.getActionData(document);
		// 拼凑出ajax请求地址
		String ajaxUrl = HtmlParser.getRepostUrl(rootActionData);

		String owner = WeiboParser.getWeiboOwnerName(document);
		String publishTime = WeiboParser.getWeiboPublishTime(document);
		RepostInfo info = new RepostInfo(1, owner, amount, publishTime, "null", ajaxUrl, "null");
		RepostDAO.record(info);
		RepostLinkQueue.add(ajaxUrl);

		repostService.execute(new Thread(new RepostSpider()));
		repostService.execute(new Thread(new RepostSpider()));
		repostService.execute(new Thread(new RepostSpider()));
		repostService.execute(new Thread(new RepostSpider()));

	}

	private static void crawlComment(ExecutorService commentService, String weiboUrl) {
		// 获取评论页面
		String url = weiboUrl + "?type=comment";
		Document document = WebFetcher.fetchHtml(url);
		// 获取root-action-data
		String rootActionData = HtmlParser.getActionData(document);
		// 获取评论的ajax地址
		String ajaxUrl = HtmlParser.getCommentUrl(rootActionData);
		// 添加地址到CommentLinkQueue中去
		CommentLinkQueue.add(ajaxUrl);

		commentService.execute(new Thread(new CommentSpider()));
		commentService.execute(new Thread(new CommentSpider()));
		commentService.execute(new Thread(new CommentSpider()));
		commentService.execute(new Thread(new CommentSpider()));
	}
}
