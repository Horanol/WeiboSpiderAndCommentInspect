package com.Weibo.Beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class WeiboInfo implements Info {
	private int id;
	private int userId;
	private String createTime;
	private String fetchTime;
	private String content;
	private int repostCount;
	private int commentCount;
	private int hotValue;
	private static AtomicInteger autoId = new AtomicInteger(1);
	
	public WeiboInfo(int userId, String createTime, String fetchTime, String content, int repostCount,
			int commentCount) {
		this.id = autoId.getAndIncrement();
		this.userId = userId;
		this.createTime = createTime;
		this.fetchTime = fetchTime;
		this.content = content;
		this.repostCount = repostCount;
		this.commentCount = commentCount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getCreateTime() {
		long time = Long.parseLong(createTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedTime = format.format(new Date(time));
		return formattedTime;
	}
	public long getLongCreateTime() {
		long time = Long.parseLong(createTime);
		return time;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public String getFetchTime() {
		long time = Long.parseLong(fetchTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedTime = format.format(new Date(time));
		return formattedTime;
	}
	public long getLongFetchTime() {
		long time = Long.parseLong(fetchTime);
		return time;
	}
	public void setFetchTime(String fetchTime) {
		this.fetchTime = fetchTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getRepostCount() {
		return repostCount;
	}
	public void setRepostCount(int repostCount) {
		this.repostCount = repostCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public int getHotValue() {
		return hotValue;
	}
	public void setHotValue(int hotValue) {
		this.hotValue = hotValue;
	}
	@Override
	public String toString() {
		return "WeiboInfo [id=" + id + ", userId=" + userId + ", createTime=" + getCreateTime() + ", fetchTime=" + getFetchTime()
				+ ", content=" + content + ", repostCount=" + repostCount + ", commentCount=" + commentCount + "]";
	}
	
	
}
