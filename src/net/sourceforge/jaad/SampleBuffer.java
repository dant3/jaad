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
package net.sourceforge.jaad;

/**
 * The SampleBuffer holds the decoded AAC frame. It contains the raw PCM data
 * and its format.
 * @author in-somnia
 */
public class SampleBuffer {

	/**
	 * This class represents the format of the raw PCM data stored in the
	 * sample buffer.
	 */
	public static class Format {

		private int sampleRate, channels, bitsPerSample;

		public Format(int sampleRate, int channels, int bitsPerSample) {
			this.sampleRate = sampleRate;
			this.channels = channels;
			this.bitsPerSample = bitsPerSample;
		}

		public int getSampleRate() {
			return sampleRate;
		}

		public int getChannels() {
			return channels;
		}

		public int getBitsPerSample() {
			return bitsPerSample;
		}
	}
	private final Format format;
	private byte[] data;

	public SampleBuffer() {
		data = new byte[0];
		format = new Format(0, 0, 0);
	}

	/**
	 * Returns the format of this sample buffer's data.
	 * @return the audio format
	 */
	public Format getFormat() {
		return format;
	}

	public void setFormat(int sampleRate, int channels, int bitsPerSample) {
		format.sampleRate = sampleRate;
		format.channels = channels;
		format.bitsPerSample = bitsPerSample;
	}

	/**
	 * Returns the buffer's PCM data.
	 * @return the audio data
	 */
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
