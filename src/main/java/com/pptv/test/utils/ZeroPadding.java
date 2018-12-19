package com.pptv.test.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ZeroPadding extends Padding {
	
	public ZeroPadding() { 
		super();
	}
	
	public ZeroPadding(int bSize) { 
		super(bSize);
	}
	
	@Override
	public byte[] pad(byte[] input, int start, int len) {
		if (blockSize == 0) {
			return input;
		}
		int paddedLength = ((len + blockSize - 1) / blockSize) * blockSize;
		byte[] padded = new byte[paddedLength];
		System.arraycopy(input, start, padded, 0, len);
		return padded;
	}

	@Override
	public byte[] unpad(byte[] input, int start, int len) {
		return Arrays.copyOfRange(input, start, start + len);
	}

	@Override
	public StreamRunnable pad() throws IOException {
		return new StreamRunnable() {
			@Override
			public void process(InputStream in, OutputStream out) throws IOException {
				if (blockSize == 0) {
					StreamUtil.copyStream(in, out);
					return;
				}
				byte[] block = new byte[blockSize];
				while (true) {
					int read = in.read(block);
					if (read == blockSize) {
						out.write(block);
						continue;
					} else if (read == -1) {
						break;
					} else {
						Arrays.fill(block, read, blockSize, CryptoUtil.ZERO_BYTE);
						out.write(block);
						break;
					}
				}
			}
		};
	}

	@Override
	public StreamRunnable unpad() throws IOException {
		return new StreamRunnable() {
			@Override
			public void process(InputStream in, OutputStream out) throws IOException {
				StreamUtil.copyStream(in, out);
			}
		};
	}
}