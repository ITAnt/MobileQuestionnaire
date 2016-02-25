package com.onion.paper.model.web;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Paper extends BmobObject implements Serializable {

	private static final long serialVersionUID = -5215059264538281160L;

	// 创建者
	private String userId;

	// 问卷名
	private String paperName;

	// 字段
	private String column;

	// 提交的问卷
	private String content;

	private Boolean hasSubmit;

	public Boolean getHasSubmit() {
		return hasSubmit;
	}

	public void setHasSubmit(Boolean hasSubmit) {
		this.hasSubmit = hasSubmit;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPaperName() {
		return paperName;
	}

	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
