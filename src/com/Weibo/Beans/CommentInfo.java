package com.Weibo.Beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class CommentInfo implements Info {
	private int id;
	private int weiboId;
	private long userId;
	private String createTime;
	private String fetchTime;
	private String content;
	private int vote;
	private static AtomicInteger autoId = new AtomicInteger(1);

	public CommentInfo(int weiboId, long userId, String createTime, String fetchTime, String content, int vote) {
		this.id = autoId.getAndIncrement();
		this.weiboId = weiboId;
		this.userId = userId;
		this.createTime = createTime;
		this.fetchTime = fetchTime;
		this.content = content;
		this.vote = vote;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * 返回格式化的评论创建时间
	 * 
	 * @return
	 */
	public String getCreateTime() {
		long time = 0;
		try {
			time = Long.parseLong(createTime);
		} catch (Exception e) {
			System.out.println("failed!	createTime = " + createTime);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
		String formattedTime = format.format(new Date(time));
		return formattedTime;
	}

	/**
	 * 返回long型的评论创建时间
	 * 
	 * @return
	 */
	public long getLongCreateTime() {
		long time = Integer.parseInt(createTime);
		return time;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 返回格式化的评论抓取时间
	 * 
	 * @return
	 */
	public String getFetchTime() {
		long time = Long.parseLong(fetchTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedTime = format.format(new Date(time));
		return formattedTime;
	}

	public long getLongFetchTime() {
		long time = Integer.parseInt(fetchTime);
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

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	@Override
	public String toString() {
		return "CommentInfo [id=" + id + ", weiboId=" + weiboId + ", userId=" + userId + ", createTime="
				+ getCreateTime() + ", fetchTime=" + getFetchTime() + ", content=" + content + ", vote=" + vote + "]";
	}

}
