package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.api.Track.Type;

public class Frame implements Comparable<Frame> {

	//TODO: get type out of track class
	private final Type type;
	private final long offset, size;
	private final double time;

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
}
