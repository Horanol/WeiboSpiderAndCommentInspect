package com.Weibo.Beans;

import java.util.concurrent.atomic.AtomicInteger;

public class UserInfo {
	private int id;
	private String name;
	private int fansCount;
	private int followCount;
	private int weiboCount;
	private boolean certified;
	private static AtomicInteger autoId = new AtomicInteger(1);
	
	public UserInfo(String name, int fansCount, int followCount, int weiboCount, boolean certified) {
		this.id = autoId.getAndIncrement();
		this.name = name;
		this.fansCount = fansCount;
		this.followCount = followCount;
		this.weiboCount = weiboCount;
		this.certified = certified;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFansCount() {
		return fansCount;
	}
	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}
	public int getFollowCount() {
		return followCount;
	}
	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}
	public int getWeiboCount() {
		return weiboCount;
	}
	public void setWeiboCount(int weiboCount) {
		this.weiboCount = weiboCount;
	}
	public boolean isCertified() {
		return certified;
	}
	public void setCertified(boolean certified) {
		this.certified = certified;
	}
	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", name=" + name + ", fansCount=" + fansCount + ", followCount=" + followCount
				+ ", weiboCount=" + weiboCount + ", certified=" + certified + "]";
	}

	
	
}
