package com.pptv.test.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamRunnable implements Runnable {
	private InputStream src;
	private OutputStream dest;
	private InputStream related;
	private boolean closeOnEnd;
	private IOException failure;	
	
	public abstract void process(InputStream in, OutputStream out) throws IOException;
	
	@Override
	public final void run() {
		try {
			process(src, dest);
		} catch (IOException e) {
			e.printStackTrace();
			StreamUtil.quitelyClose(dest);
			StreamUtil.quitelyClose(related);
			flagFailure(e);
		} finally {
			StreamUtil.quitelyClose(src);
			if (closeOnEnd) {
				StreamUtil.quitelyClose(dest);
			}
		}
		
	}
	
	private synchronized final void flagFailure(IOException failureReason) {
		failure = failureReason;
	}
	
	public synchronized final void throwIfFailed() throws IOException {
		if (failure != null) {
			throw failure;
		}
	}
	
	
	public synchronized final boolean didFail() {
		return (failure != null);
	}
	
	public synchronized final IOException getFailureReason() {
		return failure;
	}
	
	public final InputStream startAsync(InputStream in) throws IOException {
		src = in;
		closeOnEnd = true;
		DynamicByteQueue q = new DynamicByteQueue();
		dest = q.getOutputStream();
		related = q.getInputStream();
		startOnNewThread();
		return related;
	}
	
	/**
	 * This can potentially use up a crazy high amount of RAM, since it buffers the entire output before returning,
	 * so be careful what input streams you use it with.
	 * @param in
	 * @return 
	 * @throws IOException
	 */
	public final InputStream startSync(InputStream in) throws IOException {
		DynamicByteQueue q = new DynamicByteQueue();
		startSync(in, q.getOutputStream(), true);
		return q.getInputStream();
	}
	
	private void startOnNewThread() {
		Thread thread = new Thread(this); //this should be replaced to use thread pools of some sort
		thread.start();
	}
	
	public final ByteProcess asByteProcess(boolean async) {
		return new ByteProcess(async, this);
	}
	
	public final byte[] startSync(byte[] input) throws IOException {
		return asByteProcess(false).add(input).finishSync();
	}

	public final OutputStream startSync(InputStream in, OutputStream out) throws IOException {
		return startSync(in, out, true);
	}
	
	public final OutputStream startSync(InputStream in, OutputStream out, boolean closeWhenDone) throws IOException {
		src = in;
		dest = out;
		closeOnEnd = closeWhenDone;
		run();
		if (didFail()) {
			throw getFailureReason();
		}
		return out;
	}
	
	public final void startAsync(InputStream in, OutputStream out, boolean closeWhenDone) {
		src = in;
		dest = out;
		closeOnEnd = closeWhenDone;
		startOnNewThread();
	}
}