package com.Weibo.Services;

import java.util.List;

import org.jsoup.nodes.Document;

import com.Weibo.Beans.CommentInfo;
import com.Weibo.Dao.DBHandler;
import com.Weibo.Queues.CommentLinkQueue;
import com.Weibo.Queues.VistedCommentLinkSet;
import com.Weibo.Utils.CommentParser;
import com.Weibo.Utils.JsonParser;

public class CommentHandler {
	public boolean handle(String jsonContent) {
		Document document = JsonParser.getHtmlDocFromJSON(jsonContent);
		if (document != null) {
			List<CommentInfo> list = CommentParser.parseContentToBeans(document);
			for (CommentInfo commentInfo : list) {
				DBHandler.recordCommentInfo(commentInfo);
			}
			//若该页面有剩余评论页面链接，且当前页为第一页，则获取所有连接，加入未处理连接队列
			int pageCount = JsonParser.getTotalPageFromJSON(jsonContent);//先获取总评论页数，若页数大于一则继续获取其他分页地址
			int pageNum = JsonParser.getPageNumFromJSON(jsonContent);
			if (pageCount>1 && pageNum ==1) {
				List<String> pageUrls = CommentParser.getRestPageUrls(document,pageCount);//获取其他分页地址
				for (String pageLink : pageUrls) {
					setUrlQueue(pageLink);
				}
			}
			return true;
		}
		return false;
	}

	private boolean setUrlQueue(String url) {
		if (VistedCommentLinkSet.contains(url)) {
			System.out.println("Comment Contained！");
			return false;
		}
		CommentLinkQueue.add(url);
		return true;
	}
}
