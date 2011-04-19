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
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class NeroMetadataTagsBox extends BoxImpl {

	private final Map<String, String> pairs;

	public NeroMetadataTagsBox() {
		super("Nero Metadata Tags Box");
		pairs = new HashMap<String, String>();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		in.skipBytes(12); //meta box
		left -= 12;

		String key, val;
		int len;
		while(left>0) {
			in.skipBytes(3); //x80 x00 x06/x05
			key = in.readUTFString((int) left, MP4InputStream.UTF8);
			in.skipBytes(5); //0x00 0x01 0x00 0x00 0x00
			len = in.read();
			val = in.readString(len);
			pairs.put(key, val);
		}
	}

	public Map<String, String> getPairs() {
		return pairs;
	}
}