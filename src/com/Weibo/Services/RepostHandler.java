package com.Weibo.Services;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;

import com.Weibo.Beans.CommentInfo;
import com.Weibo.Beans.RepostInfo;
import com.Weibo.Dao.CommentDAO;
import com.Weibo.Dao.RepostDAO;
import com.Weibo.Exceptions.EmptyPageException;
import com.Weibo.Exceptions.LogicException;
import com.Weibo.Queues.RepostLinkQueue;
import com.Weibo.Queues.VisitedRepostLinkSet;
import com.Weibo.Queues.VisitedReposterIDSet;
import com.Weibo.Utils.JsonParser;
import com.Weibo.Utils.RepostParser;

public class RepostHandler {
	/**
	 *  持久化转发数据，更新转发队列
	 * @param content
	 * @throws LogicException 
	 */
	public void handle(String jsonContent) throws LogicException {
		Document document = JsonParser.getHtmlDocFromJSON(jsonContent);
		if (document != null) {
			Map<RepostInfo, CommentInfo> infos = RepostParser.parseContentToBeans(document);
			for(Entry<RepostInfo, CommentInfo> entry:infos.entrySet()){
				RepostInfo repostInfo = entry.getKey();
				CommentInfo commentInfo = entry.getValue();
				
				if (setUrlQueue(repostInfo.getRepostUrl())) {
					if (!VisitedReposterIDSet.contains(repostInfo.getUserId())) {
						RepostDAO.record(repostInfo);
						CommentDAO.record(commentInfo);
						VisitedReposterIDSet.add(repostInfo.getUserId());
					}
				}
			}
			addHotLink(jsonContent, document);
		}
	}

	private boolean setUrlQueue(String url) {
		if (VisitedRepostLinkSet.contains(url)) {
			System.out.println("Contains!!!!");
			return false;
		}
		RepostLinkQueue.add(url);
		return true;
	}
	private void addHotLink(String jsonContent,Document document) {
		//若该请求有热门转发，且当前页为热门转发第一页，则获取所有热门转发连接，加入未处理连接队列
		int pageCount = JsonParser.getTotalPageFromJSON(jsonContent);//先获取热门页数，若页数大于一则继续获取其他分页地址
		int pageNum = JsonParser.getPageNumFromJSON(jsonContent);
		if (pageCount>1 && pageNum ==1) {
			List<String> pageUrls = RepostParser.getRestPageUrls(document,pageCount);//获取其他分页地址
			for (String pageLink : pageUrls) {
				setUrlQueue(pageLink);
			}
		}
	}
}
