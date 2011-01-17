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
package jaad.util.mp4.boxes;

import jaad.util.mp4.ContainerBoxImpl;
import jaad.util.mp4.MP4InputStream;
import java.io.IOException;

public class AudioSampleEntryBox extends ContainerBoxImpl {

	private int channelCount, sampleSize, sampleRate;

	@Override
	public void decode(MP4InputStream in) throws IOException {
		in.skipBytes(6); //reserved
		in.skipBytes(2); //dataReferenceIndex
		in.skipBytes(8); //reserved
		channelCount = (int) in.readBytes(2);
		sampleSize = (int) in.readBytes(2);
		in.skipBytes(4);
		sampleRate = (int) in.readBytes(2);
		in.skipBytes(2);
		left -= 28;
		super.decode(in);
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
