package com.Weibo.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import net.sf.json.JSONObject;

public class JsonParser {
	/**
	 * 从json中截取html格式的文档
	 * 
	 * @param content
	 * @return
	 */
	public static Document getHtmlDocFromJSON(String content) {
		Document htmlDoc = null;
		try {
			JSONObject object = JSONObject.fromObject(content);
			JSONObject dataObject = object.getJSONObject("data");
			String htmlString = dataObject.getString("html");
			htmlDoc = Jsoup.parse(htmlString);
		} catch (Exception e) {
			System.err.println(Thread.currentThread().getName()+":   exception from method : JsonParser.getHtmlDocFromJSON retrying...");
 		}
		return htmlDoc;
	}

	/**
	 * 取得这个热门转发有多少页
	 * 
	 * @param content
	 * @return
	 */
	public static int getTotalPageFromJSON(String content) {
		JSONObject object = JSONObject.fromObject(content);
		JSONObject dataObject = object.getJSONObject("data");
		JSONObject pageObject = dataObject.getJSONObject("page");
		String pageCount = pageObject.getString("totalpage");
		return Integer.parseInt(pageCount);
	}
	/**
	 * 取得当前页数
	 * 
	 * @param content
	 * @return
	 */
	public static int getPageNumFromJSON(String content) {
		JSONObject object = JSONObject.fromObject(content);
		JSONObject dataObject = object.getJSONObject("data");
		JSONObject pageObject = dataObject.getJSONObject("page");
		String pageCount = pageObject.getString("pagenum");
		return Integer.parseInt(pageCount);
	}

}
