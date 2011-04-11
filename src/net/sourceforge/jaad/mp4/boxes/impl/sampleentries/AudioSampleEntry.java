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
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;

public class AudioSampleEntry extends SampleEntry {

	public static final long TYPE_MP4A = 1836069985; //mp4a
	public static final long TYPE_AMR = 1935764850; //samr
	public static final long TYPE_AMR_WB = 1935767394; //sawb
	public static final long TYPE_EVCR = 1936029283; //sevc
	public static final long TYPE_QCELP = 1936810864; //sqcp
	public static final long TYPE_SMV = 1936944502; //ssmv
	private int channelCount, sampleSize, sampleRate;

	public AudioSampleEntry() {
		super("Audio Sample Entry");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		in.skipBytes(8); //reserved
		channelCount = (int) in.readBytes(2);
		sampleSize = (int) in.readBytes(2);
		in.skipBytes(2); //pre-defined: 0
		in.skipBytes(2); //reserved
		sampleRate = (int) ((in.readBytes(4)>>16)&0xFFFF);
		left -= 20;

		readChildren(in);
	}

	public int getChannelCount() {
		return channelCount;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getSampleSize() {
		return sampleSize;
	}
}
