package com.Weibo.Fetcher;

import com.Weibo.Exceptions.WebException;

public class CommentFetcher {
	public String fetch(String url) throws WebException {
		String jsonContent = fetchJSONContent(url);
		return jsonContent;
	}
	/**
	 * 通过连接获取JSON数据，然后把该连接放在已访问过的连接集合中
	 * @return
	 * @throws WebException 
	 */
	private String fetchJSONContent(String newLink) throws WebException {
		String content = null;
		content = WebFetcher.fetchJSON(newLink);
		if (content == null || content.length() == 0) {
			throw new WebException();
		}
		return content;
	}
}
