package com.Weibo.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.Weibo.Beans.CommentInfo;

public class CommentParser {
	/**
	 * 获取剩下的评论分页地址
	 * 
	 * @return
	 */
	public static List<String> getRestPageUrls(Document document,int pageCount) {
		String cssQuery = "a[action-type=\"feed_list_page\"]";
		List<String> urls = new LinkedList<>();
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		//action-data="id=3847795440280387&page=2"
		StringBuilder actionData = null;
		if (element != null) {
		     actionData =new StringBuilder( element.attr("action-data"));
		     actionData.deleteCharAt(actionData.length()-1);
		     //当总热门转发页数大于2时，生成其他页url地址
		     for(int i = 2;i<=pageCount;i++){
		    	 String newUrl = "http://weibo.com/aj/v6/comment/big?ajwvr=6&"+actionData+i;
		    	 urls.add(newUrl);
		    	 //http://weibo.com/aj/v6/comment/big?ajwvr=6&id=3847795440280387&page=2
		     }
		}
		return urls;
	}

	public static List<CommentInfo> parseContentToBeans(Document document) {
		List<CommentInfo> list = new LinkedList<>();
		List<Element> elements = splitSingleComment(document);
		if (elements == null) {
			return null;
		}
		for (Element singleElem : elements) {
			String content = getContent(singleElem);
			if (content == null || content.length() == 0) {
				continue;
			}
			int weiboId = 0;
			long userId = getUserId(singleElem);
			String createTime = getCommentTime(singleElem);
			String fetchTime = String.valueOf(System.currentTimeMillis());
			int vote = getVote(singleElem);
			CommentInfo info = new CommentInfo(weiboId, userId, createTime, fetchTime, content, vote);
			list.add(info);
		}
		return list;
	}

	/**
	 * 获取评论人的Id
	 * 
	 * @param element
	 * @return
	 */
	private static long getUserId(Element element) {
		String cssQuery = "a[usercard]";
		Element nameElement = element.select(cssQuery).first();
		String idText = null;
		if (nameElement != null) {
			idText = nameElement.attr("usercard");
			idText = idText.substring(idText.indexOf("id=") + 3);
		}
		long id = Long.parseLong(idText);
		return id;
	}

	/**
	 * 获取评价人的名字
	 * 
	 * @param element
	 * @return
	 */
	private static String getUserName(Element element) {
		String cssQuery = "a[usercard]";
		Element nameElement = element.select(cssQuery).first();
		String name = null;
		if (nameElement != null) {
			name = nameElement.text().trim();
		}
		return name;
	}

	private static String getContent(Element element) {
		StringBuilder contentText = null;
		String content = null;
		String cssQuery = "div[class=\"WB_text\"]";
		Element contentElem = element.select(cssQuery).first();
		if (contentElem != null) {
			contentText = new StringBuilder(contentElem.ownText().trim());
		}
		if (contentText != null) {
			int beginIndex = contentText.indexOf("：");
			int endIndex = contentText.indexOf("回复");

			if (beginIndex != -1) {
				if (endIndex != -1) {// 去掉回复别人的话语，也就是"回复"这样的形式
					content = contentText.substring(beginIndex + 1, endIndex);
				} else {// 去除前面人的评价，也就是"//@某人 XX"这样的形式
					endIndex = contentText.indexOf("//");
					if (endIndex != -1) {//若有//，去掉//后面的评价
						content = contentText.substring(beginIndex + 1, endIndex).trim();
					} else {//若没有，去掉开头的"："
						content = contentText.substring(beginIndex + 1).trim();
					}
				}

			}
		}
		return content;
	}

	/**
	 * 获取评论时间
	 * 
	 * @return
	 */
	private static String getCommentTime(Element element) {
		String timeText = null;
		String commentTime = null;
		DateFormat format = null;
		Calendar calendar = Calendar.getInstance();
		String cssQuery = "div[class=\"WB_from S_txt2\"]";
		Element timeElement = element.select(cssQuery).first();
		timeText = timeElement.text().trim();
		//处理日期成 2015-5-29 14:17 形式
		if (timeText.contains("今天")) {// 处理如"今天 20:10"的情况
			format = new SimpleDateFormat("yyyy-M-dd");
			Date date = calendar.getTime();
			timeText = timeText.replace("今天", format.format(date));
		}else if (timeText.contains("月")) { // 处理如"4月27日 12:38"的情况
			timeText = timeText.replace("月", "-");
			timeText = timeText.replace("日", "");
			String year = String.valueOf(calendar.get(Calendar.YEAR));
			timeText = year + "-" + timeText;
		}else if (timeText.contains("分钟")) {//处理如"56分钟前"的情况
			int minute = Integer.valueOf(timeText.substring(0, timeText.indexOf("分钟")));
			format = new SimpleDateFormat("yyyy-M-dd HH:mm");
			Date d = new Date();
			timeText = format.format(new Date(d.getTime() - minute * 60 * 1000));
		}else if (timeText.contains("秒")) {//处理如"56秒前"的情况
			int second = Integer.valueOf(timeText.substring(0, timeText.indexOf("秒")));
			format = new SimpleDateFormat("yyyy-M-dd HH:mm");
			Date d = new Date();
			timeText = format.format(new Date(d.getTime() - second * 1000));
		}
		// 把如"2015-5-29 14:17"的日期转化成时间戳返回
		try {
			format = new SimpleDateFormat("yyyy-M-dd HH:mm");
			Date date = format.parse(timeText);
			commentTime = String.valueOf(date.getTime());
		} catch (ParseException e) {

		}
		return commentTime;
	}

	private static int getVote(Element element) {
		String cssQuery = "span[node-type=\"like_status\"]";
		Element voteElement = element.select(cssQuery).first();
		String voteText = null;
		int vote = 0;
		if (voteElement != null) {
			Element emElement = voteElement.child(1);
			voteText = emElement.text().trim();
		}
		try {
			vote = Integer.parseInt(voteText);
		} catch (Exception e) {
		}
		return vote;
	}

	private static List<Element> splitSingleComment(Document document) {
		String cssQuery = "div[comment_id]";
		Elements elements = document.select(cssQuery);
		List<Element> elementList = null;
		if (elements != null) {
			elementList = new LinkedList<>();
			for (int i = 0; i < elements.size(); i++) {
				elementList.add(elements.get(i));
			}
		}
		return elementList;
	}
}
