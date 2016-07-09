package com.Weibo.Beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class RepostInfo implements Info {
	private int id;
	private long userId;
	private String reposerName;
	private int repostNum;
	private String repostTime;
	private String repostFrom;
	private String repostUrl;
	private String repostReason;
	private static AtomicInteger autoId = new AtomicInteger(1);
	public String getRepostReason() {
		return repostReason;
	}
	public void setRepostReason(String repostReason) {
		this.repostReason = repostReason;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getId() {
		return id;
	}
	public String getRepostFrom() {
		return repostFrom;
	}
	public void setRepostFrom(String repostFrom) {
		this.repostFrom = repostFrom;
	}
	public String getRepostUrl() {
		return repostUrl;
	}
	public void setRepostUrl(String repostUrl) {
		this.repostUrl = repostUrl;
	}
	/**
	 *  返回格式化的日期时间
	 * @return
	 */
	public String getRepostTime() {
		long time = Long.parseLong(repostTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedTime = format.format(new Date(time));
		return formattedTime;
	}
	public long getLongRepostTime() {
		long time = Integer.parseInt(repostTime);
		return time;
	}
	public String getStringRepostTime() {
		return repostTime;
	}
	public void setRepostTime(String time) {
		this.repostTime = time;
	}
	public String getReposerName() {
		return reposerName;
	}
	public void setReposerName(String reposerName) {
		this.reposerName = reposerName;
	}
	public int getRepostNum() {
		return repostNum;
	}
	public void setRepostNum(int repostNum) {
		this.repostNum = repostNum;
	}
	public RepostInfo(long userId,String reposerName, int repostNum,String repostTime,String repostFrom,String repostUrl,String repostReason) {
		this.id = autoId.getAndIncrement();
		this.userId = userId;
		this.reposerName = reposerName;
		this.repostNum = repostNum;
		this.repostTime = repostTime;
		this.repostFrom = repostFrom;
		this.repostUrl = repostUrl;
		this.repostReason = repostReason;
	}
	@Override
	public String toString() {
		return "RepostInfo [id=" + id + ", userId=" + userId + ", reposerName=" + reposerName + ", repostNum="
				+ repostNum + ", repostTime=" + getRepostTime() + ", repostFrom=" + repostFrom + ", repostUrl=" + repostUrl
				+ "]";
	}




}
