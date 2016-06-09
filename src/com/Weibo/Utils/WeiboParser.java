package com.Weibo.Utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeiboParser {
	public static String getWeiboOwnerName(Document document) {
		String cssQuery = "div[nick-name]";
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		String nickName = null;
		if (element != null) {
			nickName = element.attr("nick-name");
		}
		return nickName;
	}
	public static String getWeiboPublishTime(Document document) {
		String cssQuery = "a[node-type=\"feed_list_item_date\"]";
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		String publishTime = null;
		if (element != null) {
			publishTime = element.attr("date");
		}
		return publishTime;
	}
}
