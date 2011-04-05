/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad.mp4;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class MP4InputStream {

	public static final int MASK8 = 0xFF;
	public static final int MASK16 = 0xFFFF;
	public static final String UTF8 = "UTF-8";
	public static final String UTF16 = "UTF-16";
	private static final int BYTE_ORDER_MASK = 0xFEFF;
	private final InputStream in;
	private final RandomAccessFile fin;
	private long offset; //only used with InputStream

	MP4InputStream(InputStream in) {
		this.in = in;
		fin = null;
		offset = 0;
	}

	MP4InputStream(RandomAccessFile fin) {
		this.fin = fin;
		in = null;
	}

	public int read() throws IOException {
		int i = 0;
		if(in!=null) i = in.read();
		else if(fin!=null) i = fin.read();

		if(in!=null&&i!=-1) offset++;
		return i;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int i = 0;
		if(in!=null) i = in.read(b, off, len);
		else if(fin!=null) i = fin.read(b, off, len);

		if(in!=null&&i!=-1) offset += i;
		return i;
	}

	public long readBytes(int n) throws IOException {
		int i = -1;
		long result = 0;
		while(n>0&&(i = read())!=-1) {
			result = (result<<8)|(i&0xFF);
			n--;
		}
		if(i==-1) throw new EOFException();
		else return result;
	}

	public boolean readBytes(final byte[] b) throws IOException {
		int read = 0;
		int i;
		while(read<b.length) {
			i = read(b, read, b.length-read);
			if(i==-1) break;
			else read += i;
		}
		return read==b.length;
	}

	public String readString(final int n) throws IOException {
		int i = -1;
		int pos = 0;
		char[] c = new char[n];
		while(pos<n&&(i = read())!=-1) {
			c[pos] = (char) i;
			pos++;
		}
		if(i==-1) throw new EOFException();
		return new String(c, 0, pos);
	}

	public String readUTFString(int max, String encoding) throws IOException {
		byte[] b = new byte[max];
		int pos = 0;
		int i;
		while((i = read())!=0) {
			if(i==-1) break;
			b[pos] = (byte) i;
			pos++;
		}

		return new String(b, 0, pos, Charset.forName(encoding));
	}

	public String readUTFString(int max) throws IOException {
		final int i = (int) readBytes(2);
		return readUTFString(max, (i==BYTE_ORDER_MASK) ? UTF16 : UTF8);
	}

	public double readFixedPoint(int len, int mask) throws IOException {
		final long l = readBytes(len);
		final long mantissa = (l&mask)<<52;
		final long exponent = l&mask;
		return Double.longBitsToDouble(mantissa|exponent);
	}

	public boolean skipBytes(final long n) throws IOException {
		long l = 0;
		if(in!=null) {
			l = in.skip(n);
			offset += n;
		}
		else if(fin!=null) l = fin.skipBytes((int) n);

		return l==n;
	}

	public long getOffset() throws IOException {
		long l = -1;
		if(in!=null) l = offset;
		else if(fin!=null) l = fin.getFilePointer();
		return l;
	}

	void close() throws IOException {
		in.close();
	}
}