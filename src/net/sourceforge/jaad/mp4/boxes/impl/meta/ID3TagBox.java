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
package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

//TODO: use nio ByteBuffer instead of array
public class ID3TagBox extends FullBox {

	private String language;
	private byte[] id3Data;

	public ID3TagBox() {
		super("ID3 Tag Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		//1 bit padding, 5*3 bits language code (ISO-639-2/T)
		final long l = in.readBytes(2);
		char[] c = new char[3];
		c[0] = (char) (((l>>10)&31)+0x60);
		c[1] = (char) (((l>>5)&31)+0x60);
		c[2] = (char) ((l&31)+0x60);
		language = new String(c);
		left -= 2;

		id3Data = new byte[(int) left];
		in.readBytes(id3Data);
		left = 0;
	}

	public byte[] getID3Data() {
		return id3Data;
	}

	public String getLanguage() {
		return language;
	}
}
