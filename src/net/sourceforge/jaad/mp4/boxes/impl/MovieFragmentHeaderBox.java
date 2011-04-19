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

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * The movie fragment header contains a sequence number, as a safety check. The
 * sequence number usually starts at 1 and must increase for each movie fragment
 * in the file, in the order in which they occur. This allows readers to verify
 * integrity of the sequence; it is an error to construct a file where the
 * fragments are out of sequence.
 */
public class MovieFragmentHeaderBox extends FullBox {

	private long sequenceNumber;

	public MovieFragmentHeaderBox() {
		super("Movie Fragment Header Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		sequenceNumber = in.readBytes(4);
		left -= 4;
	}

	/**
	 * The ordinal number of this fragment, in increasing order.
	 */
	public long getSequenceNumber() {
		return sequenceNumber;
	}
}
