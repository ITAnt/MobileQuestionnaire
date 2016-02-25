package com.onion.paper.model.web;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {

	private static final long serialVersionUID = 7961725199997143371L;
	private String sex;
	private Integer age;
	private String imageUrl;
	private Boolean hasBeenFavorited;
	private String phoneName;
	private String androidRelease;

	private String phoneNumber;
	private String nickName;
	private String thePassword;
	private String location;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getThePassword() {
		return thePassword;
	}

	public void setThePassword(String thePassword) {
		this.thePassword = thePassword;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAndroidRelease() {
		return androidRelease;
	}

	public void setAndroidRelease(String androidRelease) {
		this.androidRelease = androidRelease;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

	public Boolean getHasBeenFavorited() {
		return hasBeenFavorited;
	}

	public void setHasBeenFavorited(Boolean hasBeenFavorited) {
		this.hasBeenFavorited = hasBeenFavorited;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
