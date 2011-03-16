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
package net.sourceforge.jaad.util.mp4;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MP4InputStream {

	private final InputStream in;
	private long offset = 0;

	MP4InputStream(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		offset++;
		return in.read();
	}

	public long readBytes(int n) throws IOException {
		int i = -1;
		long result = 0;
		while(n>0&&(i = in.read())!=-1) {
			result = (result<<8)|(i&0xFF);
			offset++;
			n--;
		}
		if(i==-1) {
			throw new EOFException();
		}
		return result;
	}

	public String readString(int n) throws IOException {
		int c = -1;
		final StringBuilder sb = new StringBuilder();
		while(n>0&&(c = in.read())!=-1) {
			sb.append((char) c);
			offset++;
			n--;
		}
		if(c==-1) throw new EOFException();
		return sb.toString();
	}

	public boolean readBytes(byte[] b) throws IOException {
		int read = 0;
		int i;
		while(read<b.length) {
			i = in.read(b, read, b.length-read);
			if(i==-1) break;
			else {
				read += i;
				offset += i;
			}
		}
		return read==b.length;
	}

	public void skipBytes(long n) throws IOException {
		offset += n;
		in.skip(n);
	}

	public long getOffset() {
		return offset;
	}

	void close() throws IOException {
		in.close();
	}
}
