package com.jzsec.quotation.message;


public class Request implements Message{

	private int msgTpye;
	private long createTime = System.currentTimeMillis();
	private int heartbeat;
	private String sourceId;
	private String targetId;
	private String password;
	private String version;
	public Request() {
	}

	public Request(int msgTpye, long createTime, int heartbeat, String sourceId, String targetId, String password, String version) {
		this.msgTpye = msgTpye;
		this.createTime = createTime;
		this.heartbeat = heartbeat;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.password = password;
		this.version = version;
	}

	public int getMsgTpye() {
		return msgTpye;
	}

	public void setMsgTpye(int msgTpye) {
		this.msgTpye = msgTpye;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(int heartbeat) {
		this.heartbeat = heartbeat;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
