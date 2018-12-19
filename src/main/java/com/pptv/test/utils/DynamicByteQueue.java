package com.pptv.test.utils;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicByteQueue {

	private final int chunkSize;

	private ConcurrentLinkedQueue<byte[]> byteQueue;
	private AtomicInteger chunkCount;
	private Semaphore chunkSemaphore;
	
	private Object writeLock;
	private byte[] front;
	private volatile boolean isDoneWriting; //volatile because it is modified by the writing thread and read by the reading thread
	private volatile int lastChunkIndex; //volatile because it is modified by the writing thread and read by the reading thread
	
	private Object readLock;
	private int firstChunkIndex; //only ever modified and read by the reader no need to be volatile
	private boolean isDoneReading; //only ever modified and read by the reader no need to be volatile
	

	private OutputStream out = new OutputStream() {

		@Override
		public void write(int b) {
			byte[] bytes = new byte[]{ (byte)(b & 0xff) };
			write(bytes);
		}
		
		@Override
		public void write(byte[] bytes) {
			DynamicByteQueue.this.write(bytes);
		}
		
		@Override
		public void write(byte[] bytes, int off, int len) {
			DynamicByteQueue.this.write(bytes, off, len);
		}
		
		public void close() {
			doneWriting();
		}
	};

	private InputStream in = new InputStream() {
		@Override
		public int read() {
			byte[] single = new byte[1];
			if (DynamicByteQueue.this.read(single) == -1) {
				return -1;
			} else {
				return single[0] & 0xff;
			}
		}

		@Override
		public int read(byte[] in) {
			return DynamicByteQueue.this.read(in);
		}

		@Override
		public int read(byte[] in, int off, int len) {
			return DynamicByteQueue.this.read(in, off, len);
		}

		@Override
		public boolean markSupported() {
			return false;
		}

		@Override
		public long skip(long n) {
			return DynamicByteQueue.this.skip(n);
		}

		@Override
		public int available() {
			return DynamicByteQueue.this.available();
		}
		
		public void close() {
			doneReading();
		}
	};

	public DynamicByteQueue() {
		this(1024);
	}

	public DynamicByteQueue(int sizeOfChunks) {
		byteQueue = new ConcurrentLinkedQueue<byte[]>();
		lastChunkIndex = 0;
		firstChunkIndex = 0;
		isDoneWriting = false;
		readLock = new Object();
		writeLock = new Object();
		chunkCount = new AtomicInteger(0);
		chunkSize = sizeOfChunks;
		front = new byte[chunkSize];
		isDoneReading = false;
		chunkSemaphore = new Semaphore(0);
	}

	public void write(byte[] bytes) {
		write(bytes, 0, bytes.length);
	}

	public void write(byte[] bytes, int start, int length) {
		synchronized (writeLock) {
			if (isDoneWriting) {
				return;
			}
			int srcPos = start;
			int remaining = length;
			while (remaining > 0) {
				int bufferRemaining = chunkSize - lastChunkIndex;
				int bytesWritten = remaining > bufferRemaining ? bufferRemaining : remaining;
				System.arraycopy(bytes, srcPos, front, lastChunkIndex, bytesWritten);
				srcPos += bytesWritten;
				remaining -= bytesWritten;
				lastChunkIndex += bytesWritten;
				if (lastChunkIndex == chunkSize) {
					lastChunkIndex = 0;
					byteQueue.add(front);
					front = new byte[chunkSize];
					chunkCount.incrementAndGet();
					chunkSemaphore.release();
				}
			}
		}
	}

	public void doneWriting() {
		synchronized (writeLock) {
			if (!isDoneWriting) {
				chunkCount.incrementAndGet();
				byteQueue.add(front);
				isDoneWriting = true;
				chunkSemaphore.release();
			}
		}
	}
	
	public void doneReading() {
		synchronized (readLock) {
			if (isDoneReading) {
				return;
			}
			if (!isDoneWriting) {
				doneWriting();
			}
			skip(available());
			isDoneReading = true;
		}
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public OutputStream getOutputStream() {
		return out;
	}

	public long skip(long bytesToSkip) {
		long bytesSkipped = 0;
		int val;
		while (bytesToSkip > 0) {
			val = innerReadOrSkip(null, 0, (bytesToSkip > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) bytesToSkip, true);
			if (val == -1) {
				return bytesSkipped;
			}
			bytesSkipped += val;
			bytesToSkip -= val;
		}
		return bytesSkipped;
	}

	public int read(byte[] bytes) {
		return read(bytes, 0, bytes.length);
	}

	public int read(byte[] bytes, int start, int length) {
		return innerReadOrSkip(bytes, start, length, false);
	}

	public int available() {
		synchronized (readLock) {
			if (isDoneReading) {
				return 0;
			}
			int chunks = chunkCount.get();
			if (isDoneWriting) {
				if (chunks < 2) {
					return lastChunkIndex - firstChunkIndex;
				} else {
					return (chunks - 1) * chunkSize - firstChunkIndex + lastChunkIndex; 
				}
			} else {
				if (chunks < 2) {
					return 0;
				} else {
					return (chunks - 1) * chunkSize - firstChunkIndex;
				}
			}
			
		}
	}

	private int innerReadOrSkip(byte[] bytes, int start, int length, boolean skip) {
		synchronized (readLock) {
			if (isDoneReading) {
				return -1;
			}
			int dataRead = 0;
			int destPos = start;
			int remaining = length;
			while (dataRead < length) {
				try {
					chunkSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				int lastIndex = chunkSize;
				boolean isOnLastChunk = isDoneWriting && chunkCount.get() == 1;
				if (isOnLastChunk) {
					lastIndex = lastChunkIndex;
				}
				
				int bytesRead = coreReadOrSkip(bytes, remaining, destPos, lastIndex - firstChunkIndex, byteQueue.peek(), skip);
				remaining -= bytesRead;
				dataRead += bytesRead;
				destPos += bytesRead;
				if (firstChunkIndex == lastIndex) {
					firstChunkIndex = 0;
					byteQueue.poll();
					chunkCount.decrementAndGet();
					if (isOnLastChunk) {
						isDoneReading = true;
						break;
					}
				} else {
					//we haven't finished consuming this chunk
					chunkSemaphore.release();
				}
			}
			return dataRead;
		}
	}

	private int coreReadOrSkip(byte[] bytes, int length, int destPos, int bufferRemaining, byte[] buffer, boolean skip) {
		int bytesRead = length > bufferRemaining ? bufferRemaining : length;
		if (!skip) {
			System.arraycopy(buffer, firstChunkIndex, bytes, destPos, bytesRead);
		}
		Arrays.fill(buffer, firstChunkIndex, firstChunkIndex + bytesRead, CryptoUtil.ZERO_BYTE);
		firstChunkIndex += bytesRead;
		return bytesRead;
	}
	
	public boolean isDoneWriting() {
		return isDoneWriting;
	}
}