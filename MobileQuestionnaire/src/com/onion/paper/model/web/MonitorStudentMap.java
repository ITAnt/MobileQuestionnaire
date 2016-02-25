package com.onion.paper.model.web;

import cn.bmob.v3.BmobObject;

public class MonitorStudentMap extends BmobObject {

	private static final long serialVersionUID = -581553894012921956L;

	private String monitorId;
	private String studentId;
	public String getMonitorId() {
		return monitorId;
	}
	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
}
