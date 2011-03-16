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
package net.sourceforge.jaad.util.mp4.boxes;

import net.sourceforge.jaad.util.mp4.Box;
import net.sourceforge.jaad.util.mp4.BoxFactory;
import net.sourceforge.jaad.util.mp4.BoxTypes;
import net.sourceforge.jaad.util.mp4.FullBox;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import java.io.IOException;

public class SampleDescriptionBox extends FullBox {

	private AudioSampleEntryBox mp4a;

	public SampleDescriptionBox() {
		super();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		final int entryCount = (int) in.readBytes(4);
		left -= 4;
		Box box;
		for(int i = 0; i<entryCount; i++) {
			box = BoxFactory.parseBox(this, in);
			left -= box.getSize();
			if(box.getType()==BoxTypes.AUDIO_SAMPLE_ENTRY_BOX) {
				mp4a = (AudioSampleEntryBox) box;
				break;
			}
		}
	}

	public AudioSampleEntryBox getMP4A() {
		return mp4a;
	}
}
