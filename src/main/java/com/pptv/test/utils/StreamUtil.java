package com.pptv.test.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public static void quitelyClose(InputStream input) {
		if (input == null) {
			return;
		}
		try {
			input.close();
		} catch (IOException e) {
		}
	}

	public static void quitelyClose(OutputStream output) {
		if (output == null) {
			return;
		}
		try {
			output.close();
		} catch (IOException e) {
		}
	}

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		copyStream(input, output, 1024);
	}

	public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		while (true) {
			int read = input.read(buffer);
			if (read == -1) {
				break;
			}
			output.write(buffer, 0, read);
		}
	}
}