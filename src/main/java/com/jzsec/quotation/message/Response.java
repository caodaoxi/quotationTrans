package com.jzsec.quotation.message;

public class Response implements Message {
	private long reqId;
	private int respCode;
	private String paramType="";
	private Object responseEntry;
	private String error="";

	public Response() {

	}

	public long getReqId() {
		return reqId;
	}

	public void setReqId(long reqId) {
		this.reqId = reqId;
	}

	public int getRespCode() {
		return respCode;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	public Object getResponseEntry() {
		return responseEntry;
	}

	public void setResponseEntry(Object responseEntry) {
		this.responseEntry = responseEntry;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

}
