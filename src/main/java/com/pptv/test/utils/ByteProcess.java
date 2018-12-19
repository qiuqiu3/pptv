package com.pptv.test.utils;
import java.io.IOException;

public final class ByteProcess {
	
	private DynamicByteQueue pre;
	private DynamicByteQueue post;
	private StreamRunnable runnable;
	private boolean isAsync;
	
	public ByteProcess(boolean async, StreamRunnable r) {
		pre = new DynamicByteQueue();
		post = new DynamicByteQueue();
		runnable = r;
		isAsync = async;
		if (async) {
			runnable.startAsync(pre.getInputStream(), post.getOutputStream(), true);
		}
	}
	
	
	public ByteProcess add(byte[] bytes, int start, int len) {
		pre.write(bytes, start, len);
		return this;
	}
	public ByteProcess add(byte[] bytes) {
		return add(bytes, 0, bytes.length);
	}
	public byte[] finishSync() throws IOException {
		pre.doneWriting();
		if (!isAsync) {
			post = new DynamicByteQueue();
			runnable.startSync(pre.getInputStream(), post.getOutputStream(), true);
		} else {
			while (!post.isDoneWriting()) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					post.doneReading();
					throw new IOException(e);
				}
			}
		}
		
		byte[] result = new byte[post.available()];
		post.read(result);
		return result;
	}
	
	public byte[] finishSync(byte[] out, int start) throws IOException {
		byte[] res = finishSync();
		System.arraycopy(res, 0, out, start, res.length);
		CryptoUtil.fillWithZeroes(res);
		return out;
	}
}