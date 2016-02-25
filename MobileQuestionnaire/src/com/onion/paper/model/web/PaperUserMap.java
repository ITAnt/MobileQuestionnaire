package com.onion.paper.model.web;

import cn.bmob.v3.BmobObject;

public class PaperUserMap extends BmobObject {

	private static final long serialVersionUID = 2345680440276541352L;

	private String paperId;
	// 接受调查的人的id
	private String userId;
	private Boolean ifSubmit;

	public String getPaperId() {
		return paperId;
	}

	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIfSubmit() {
		return ifSubmit;
	}

	public void setIfSubmit(Boolean ifSubmit) {
		this.ifSubmit = ifSubmit;
	}

}
