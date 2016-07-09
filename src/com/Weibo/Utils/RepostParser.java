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
import com.Weibo.Exceptions.EmptyPageException;

public class RepostParser {
	
	public static RepostInfo getRepostInfo(Element singleRepostElem,int repostNum) {
		long reposerId = getReposerId(singleRepostElem);
		String reposerName = getReposerName(singleRepostElem);
		String repostFrom = getFromName(singleRepostElem);
		String repostTime = getRepostTime(singleRepostElem);
		String repostUrl = getRepostUrl(singleRepostElem);
		String repostReason = getRepostReason(singleRepostElem);
		RepostInfo repostInfo = new RepostInfo(reposerId,reposerName, repostNum, repostTime, repostFrom,repostUrl,repostReason);
		return repostInfo;
	}
	/**
	 * 获取热门转发其他分页地址
	 * @return
	 */
	public static List<String> getRestPageUrls(Document document,int pageCount) {
		String cssQuery = "a[action-type=\"feed_list_page\"]";
		List<String> urls = new LinkedList<>();
		Elements elements = document.select(cssQuery);
		Element element = elements.first();
		//action-data="id=3847795599148812&max_id=3847801957674345&filter=hot&page=2"
		StringBuilder actionData = null;
		if (element != null) {
		     actionData =new StringBuilder( element.attr("action-data"));
		     actionData.deleteCharAt(actionData.length()-1);
		     //当总热门转发页数大于2时，生成其他页url地址
		     for(int i = 2;i<=pageCount;i++){
		    	 String newUrl = "http://weibo.com/aj/v6/mblog/info/big?ajwvr=6&"+actionData+i;
		    	 urls.add(newUrl);
		     }
		}
		return urls;
	}
	
	/**
	 *  获取转发人名字
	 * @param element
	 * @return
	 */
	private static String getReposerName(Element element) {
		String cssQuery = "a[node-type=\"name\"]";
		Element nameElement = element.select(cssQuery).first();
		String name = null;
		if (nameElement != null) {
			 name = nameElement.text().trim();
		}
		return name;
	}
	/**
	 *  获取转发人Id
	 * @param element
	 * @return
	 */
	private static long getReposerId(Element element) {
		String cssQuery = "a[node-type=\"name\"]";
		Element nameElement = element.select(cssQuery).first();
		String idText = null;
		if (nameElement != null) {
			 idText = nameElement.attr("usercard");
			 idText = idText.substring(3);
		}
		long id = Long.parseLong(idText);
		return id;
	}
	/**
	 * 获取转发的上一级名字
	 * @param element
	 * @return
	 */
	public static String getFromName(Element element) {
		//截取//<a ... /> 部分
		String name = null;
		String patternQuery = "//<a.*?</a>";
		Pattern pattern = Pattern.compile(patternQuery);
		Matcher matcher = pattern.matcher(element.toString());
		if (matcher.find()) {
			name = matcher.group().trim();
			//截取@后面的名字
			int start = name.indexOf("@")+1;
			int end = name.indexOf("</a>");
			name = name.substring(start,end);
		}else {//若没有找到，可能是从根节点的转发，所以fromName是rootName
			name = "root";
		} 
			
		return name;
	}

	/**
	 *  获取转发量
	 * @return
	 */
	public static int getRepostNum(Element element) {
		int repostNum = 0;
		String cssQuery = "a[action-type=\"feed_list_forward\"]";
		Element numElement = element.select(cssQuery).first();
		if (numElement != null) {
			String repostNumText = numElement.text();
			repostNum = splitNum(repostNumText);
		}
		return repostNum;
	}
	/**
	 * 截取 转发 后面的数字，就是转发量
	 * 
	 * @param text
	 * @return
	 */
	private static int splitNum(String text) {
		int beginIndex = text.indexOf(" ");
		if (beginIndex > 0) {
			text = text.substring(beginIndex + 1);
			return Integer.parseInt(text);
		}
		return 0;
	}
	/**
	 *  获取转发连接
	 * @param element
	 * @return
	 */
	private static String getRepostUrl(Element element) {
		String cssQuery = "a[action-type=\"feed_list_forward\"]";
		Element urlElement = element.select(cssQuery).first();
		String actionData = urlElement.attr("action-data");
		String url = HtmlParser.getRepostUrl(actionData);
		return url;
	}
	/**
	 * 获取转发理由
	 * @param element
	 * @return
	 */
	private static String getRepostReason(Element element) {
		String cssQuery = "span[node-type=\"text\"]";
		Element reasonElement = element.select(cssQuery).first();
		StringBuilder reasonText = null;
		String reason = null;
		if (reasonElement != null) {
			reasonText = new StringBuilder(reasonElement.text().trim());
			if (reasonText.indexOf("//")!=-1) {
				reasonText.delete(reasonText.indexOf("//"), reasonText.length());
			}
			if (reasonText!=null && !reasonText.equals("转发微博") && !reasonText.equals("")) {
				reason = reasonText.toString();
			}
		}
		if (reason == null || reason.length()==0) {
			return "null";
		}
		return reason;
	}
	
	/**
	 * 获取转发时间
	 * @return
	 */
	private static String getRepostTime(Element element) {
		String repostTime = null;
		String cssQuery = "a[node-type=\"feed_list_item_date\"]";
		Element timeElement = element.select(cssQuery).first();
		repostTime = timeElement.attr("date");
		return repostTime;
	}
	/**
	 *  解析html文档，把转发量大于阈值的节点组成RepostInfo实体和CommentInfo实体，返回实体Map
	 * @param document
	 * @return
	 * @throws EmptyPageException 
	 */
	public static Map<RepostInfo, CommentInfo> parseContentToBeans(Document document) throws EmptyPageException {
		int threshold = 0;
		List<Element> singleReposts = splitSingleRepost(document);
		if (singleReposts == null || singleReposts.isEmpty()) {
			System.out.println(Thread.currentThread().getName()+":  current page is empty!!");
			throw new EmptyPageException();
		}
		Map<RepostInfo, CommentInfo> infos = new HashMap<>();
		for (Element singleRepostElem : singleReposts) {
			int repostNum = RepostParser.getRepostNum(singleRepostElem);
			if (repostNum >=threshold) {
				//组装RepostInfo
				RepostInfo repostInfo = RepostParser.getRepostInfo(singleRepostElem, repostNum);
				//组装CommentInfo
				String repostReason = repostInfo.getRepostReason();
				int vote = RepostParser.getVotes(singleRepostElem);
				CommentInfo commentInfo = new CommentInfo(0,repostInfo.getId() ,repostInfo.getStringRepostTime(), String.valueOf(System.currentTimeMillis()), repostReason, vote);
				
				infos.put(repostInfo, commentInfo);
			}
		}
		return infos;
	}
	
	private static int getVotes(Element singleRepostElem) {
		
		return 0;
	}
	private static List<Element> splitSingleRepost(Document document) {
		String cssQuery = "div[action-type=\"feed_list_item\"]";
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
