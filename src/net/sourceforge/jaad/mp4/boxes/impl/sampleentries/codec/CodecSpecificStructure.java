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
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

/**
 * The <code>CodecSpecificStructure</code> contains specific information for the
 * decoder. It is present in a <code>CodecSpecificBox</code>.
 *
 * @author in-somnia
 */
public abstract class CodecSpecificStructure {

	protected int size;
	private long vendor;
	private int decoderVersion;

	protected CodecSpecificStructure(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	void decode(MP4InputStream in) throws IOException {
		vendor = in.readBytes(4);
		decoderVersion = in.read();
	}

	public long getVendor() {
		return vendor;
	}

	public int getDecoderVersion() {
		return decoderVersion;
	}
}
