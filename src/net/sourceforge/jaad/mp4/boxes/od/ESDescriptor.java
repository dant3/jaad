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
package net.sourceforge.jaad.mp4.boxes.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class ESDescriptor extends ObjectDescriptor {

	ESDescriptor(int type, int size) {
		super(type, size);
	}

	void decode(MP4InputStream in) throws IOException {
		in.skipBytes(2);
		final int flags = in.read();
		final boolean streamDependenceFlag = (flags&(1<<7))!=0;
		final boolean urlFlag = (flags&(1<<6))!=0;
		final boolean ocrFlag = (flags&(1<<5))!=0;
		bytesRead += 3;
		if(streamDependenceFlag) {
			in.skipBytes(2);
			bytesRead += 2;
		}
		if(urlFlag) {
			final int len = in.read();
			in.skipBytes(len);
			bytesRead += len+1;
		}
		if(ocrFlag) {
			in.skipBytes(2);
			bytesRead += 2;
		}

		readChildren(in);
	}
}
