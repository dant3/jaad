package net.sourceforge.jaad.mp4.api;

import java.util.Date;

class Utils {

	private static final long DATE_OFFSET = 2082850791998l;

	static Date getDate(long time) {
		return new Date(time*1000-DATE_OFFSET);
	}

	static long bytesToLong(byte[] b, int off, int len) {
		long l = 0;
		for(int i = 0; i<len; i++) {
			l <<= 8;
			l |= b[off+i];
		}
		return l;
	}
}
