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
package net.sourceforge.jaad.mp4.api;

public class Frame implements Comparable<Frame> {

	private final Type type;
	private final long offset, size;
	private final double time;
	private byte[] data;

	Frame(Type type, long offset, long size, double time) {
		this.type = type;
		this.offset = offset;
		this.size = size;
		this.time = time;
	}

	public Type getType() {
		return type;
	}

	public long getOffset() {
		return offset;
	}

	public long getSize() {
		return size;
	}

	public double getTime() {
		return time;
	}

	public int compareTo(Frame f) {
		return (int) (time-f.time);
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
