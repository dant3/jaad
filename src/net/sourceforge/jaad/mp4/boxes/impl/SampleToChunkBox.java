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

public class SampleToChunkBox extends FullBox {

	public static class SampleToChunkEntry {

		private final long firstChunk, samplesPerChunk, sampleDescriptionIndex;

		SampleToChunkEntry(long firstChunk, long samplesPerChunk, long sampleDescriptionIndex) {
			this.firstChunk = firstChunk;
			this.samplesPerChunk = samplesPerChunk;
			this.sampleDescriptionIndex = sampleDescriptionIndex;
		}

		public long getFirstChunk() {
			return firstChunk;
		}

		public long getSampleDescriptionIndex() {
			return sampleDescriptionIndex;
		}

		public long getSamplesPerChunk() {
			return samplesPerChunk;
		}
	}
	private SampleToChunkEntry[] entries;

	public SampleToChunkBox() {
		super("Sample To Chunk Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		
		final int entryCount = (int) in.readBytes(4);
		entries = new SampleToChunkEntry[entryCount];
		left -= 4;

		long firstChunk, samplesPerChunk, sampleDescriptionIndex;
		for(int i = 0; i<entryCount; i++) {
			firstChunk = in.readBytes(4);
			samplesPerChunk = in.readBytes(4);
			sampleDescriptionIndex = in.readBytes(4);
			entries[i] = new SampleToChunkEntry(firstChunk, samplesPerChunk, sampleDescriptionIndex);
			left -= 12;
		}
	}

	public SampleToChunkEntry[] getEntries() {
		return entries;
	}
}
