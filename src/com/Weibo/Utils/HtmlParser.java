package com.Weibo.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Weibo.Beans.CommentInfo;
import com.Weibo.Beans.RepostInfo;

public class HtmlParser {
	/**
	 *  用正则表达式从Action-data中截取Mid
	 * @param actionData
	 * @return
	 */
	public static String getMid(String actionData) {
		String mid = null;
		Pattern midPattern = Pattern.compile("&mid=.*?&");
		Matcher matcher = midPattern.matcher(actionData);
		if (matcher.find()) {
			mid = matcher.group();
			mid = mid.substring(5, mid.length() - 1);
		}
		return mid;
	}
	/**
	 *  从html文档中截取目标ActonData，里面含有mid
	 * @param document
	 * @return
	 */
	public static String getActionData(Document document) {
		String cssQuery = "a[action-type=\"fl_forward\"]";
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		String actionData = null;
		if (element != null) {
			 actionData = element.attr("action-data");
		}
		return actionData;
	}

	/**
	 *  从html文件中获取转发总人数
	 * @param document
	 * @return
	 */
	public static int getAllRepostAmount(Document document) {
		String cssQuery = "span[node-type=\"forward_btn_text\"]";
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		int amount = 0;
		if (element != null) {
			String num = element.child(0).child(1).text();
			amount = Integer.parseInt(num);
		}
		return amount;
	}

	

	/**
	 * 构造Ajax请求
	 * 
	 * @param actionData
	 * @return
	 */
	public static String getRepostUrl(String actionData) {
		return "http://weibo.com/aj/v6/mblog/info/big?ajwvr=6&id=" + HtmlParser.getMid(actionData) + "&filter=hot";
				//http://weibo.com/aj/v6/mblog/info/big?ajwvr=6&id=3847795440280387&filter=hot
	}
	public static String getCommentUrl(String actionData) {
		return "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=" + HtmlParser.getMid(actionData);
	}
}
