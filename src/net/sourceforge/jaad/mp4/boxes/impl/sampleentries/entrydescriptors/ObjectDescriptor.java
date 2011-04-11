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
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.entrydescriptors;

import net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ObjectDescriptor {

	public final static int TYPE_ES_DESCRIPTOR = 3;
	public final static int TYPE_DECODER_CONFIG_DESCRIPTOR = 4;
	public final static int TYPE_DECODER_SPECIFIC_INFO_DESCRIPTOR = 5;
	protected int type, size, bytesRead;
	private List<ObjectDescriptor> children;

	static ObjectDescriptor createDescriptor(MP4InputStream in) throws IOException {
		final int tag = in.read();
		int read = 1;
		int size = 0;
		int b = 0;
		do {
			b = in.read();
			size <<= 7;
			size |= b&0x7f;
			read++;
		}
		while((b&0x80)==0x80);
		final ObjectDescriptor desc;
		switch(tag) {
			case TYPE_ES_DESCRIPTOR:
				desc = new ESDescriptor(tag, size);
				break;
			case TYPE_DECODER_CONFIG_DESCRIPTOR:
				desc = new DecoderConfigDescriptor(tag, size);
				break;
			case TYPE_DECODER_SPECIFIC_INFO_DESCRIPTOR:
				desc = new DecoderSpecificInfoDescriptor(tag, size);
				break;
			default:
				desc = new UnknownDescriptor(tag, size);
		}

		desc.decode(in);
		in.skipBytes(desc.size-desc.bytesRead);
		desc.bytesRead = read+desc.size;

		return desc;
	}

	abstract void decode(MP4InputStream in) throws IOException;

	protected ObjectDescriptor(int type, int size) {
		this.bytesRead = 0;
		this.type = type;
		this.size = size;
		children = new ArrayList<ObjectDescriptor>();
	}

	//children
	protected void readChildren(MP4InputStream in) throws IOException {
		ObjectDescriptor desc;
		while(bytesRead<size) {
			desc = createDescriptor(in);
			children.add(desc);
			bytesRead += desc.getBytesRead();
		}
	}

	public List<ObjectDescriptor> getChildren() {
		return Collections.unmodifiableList(children);
	}

	//getter
	public int getType() {
		return type;
	}

	public int getBytesRead() {
		return bytesRead;
	}
}
