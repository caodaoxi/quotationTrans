package com.jzsec.quotation.message;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RespFuture {

	private Response response;
	private CountDownLatch latch;
	private long reqId;

	public RespFuture(long reqId, CountDownLatch latch) {
		this.reqId = reqId;
		this.latch = latch;
	}

	public void fillResponse(Response response) {
		this.response = response;
		latch.countDown();
	}

	public Response getResponse(int timeout) throws IOException {
		try {
			if (-1 == timeout) {
				latch.await();
			} else {
				latch.await(timeout, TimeUnit.MILLISECONDS);
			}
			if (this.response == null) {
				throw new TimeoutException("waite resp time out");
			}
		} catch (Exception e) {
			throw new IOException(e);
		}

		return this.response;
	}

	public long getReqId() {
		return reqId;
	}

}
