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
package net.sourceforge.jaad.mp4.boxes.impl;

import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;

public class TimeToSampleBox extends FullBox {

	private long sampleDuration;

	public TimeToSampleBox() {
		super("Time To Sample Box", "stts");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		final int entryCount = (int) in.readBytes(4);
		left -= 4;
		for(int i = 0; i<entryCount; i++) {
			if(i==0) sampleDuration = in.readBytes(4);
			else in.skipBytes(4);
			in.skipBytes(4);
			left -= 8;
		}
	}

	public long getSampleDuration() {
		return sampleDuration;
	}
}