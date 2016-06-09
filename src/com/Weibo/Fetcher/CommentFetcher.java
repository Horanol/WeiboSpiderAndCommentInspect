package com.Weibo.Fetcher;

import com.Weibo.Queues.VistedCommentLinkSet;

public class CommentFetcher {
	public String fetch(String url) {
		String jsonContent = fetchJSONContent(url);
		return jsonContent;
	}
	/**
	 * 通过连接获取JSON数据，然后把该连接放在已访问过的连接集合中
	 * @return
	 */
	private String fetchJSONContent(String newLink) {
		String content = null;
		content = WebFetcher.fetchJSON(newLink);
		VistedCommentLinkSet.add(newLink);
		return content;
	}
}
