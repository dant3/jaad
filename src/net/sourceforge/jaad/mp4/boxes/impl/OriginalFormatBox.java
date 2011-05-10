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
 * The Original Format Box contains the four-character-code of the original
 * un-transformed sample description.
 *
 * @author in-somnia
 */
public class OriginalFormatBox extends FullBox {

	private long originalFormat;

	public OriginalFormatBox() {
		super("Original Format Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		originalFormat = in.readBytes(4);
		left -= 4;
	}

	/**
	 * The original format is the four-character-code of the original
	 * un-transformed sample entry (e.g. 'mp4v' if the stream contains protected
	 * MPEG-4 visual material).
	 *
	 * @return the stream's original format
	 */
	public long getOriginalFormat() {
		return originalFormat;
	}
}
