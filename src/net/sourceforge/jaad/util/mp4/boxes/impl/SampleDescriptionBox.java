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
package net.sourceforge.jaad.util.mp4.boxes.impl;

import net.sourceforge.jaad.util.mp4.boxes.BoxFactory;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import net.sourceforge.jaad.util.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.util.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.sampleentries.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.jaad.util.mp4.boxes.FullBox;

/**
 * The sample description table gives detailed information about the coding type
 * used, and any initialization information needed for that coding.
 * @author in-somnia
 */
public class SampleDescriptionBox extends FullBox {

	private List<SampleEntry> sampleEntries;

	public SampleDescriptionBox() {
		super("Sample Description Box", "stsd");
		sampleEntries = new ArrayList<SampleEntry>();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		final int entryCount = (int) in.readBytes(4);
		left -= 4;

		final HandlerBox handler = (HandlerBox) ((ContainerBox) parent.getParent().getParent()).getChild(BoxTypes.HANDLER_BOX);
		final int handlerType = handler.getHandlerType();

		SampleEntry entry;
		for(int i = 0; i<entryCount; i++) {
			entry = BoxFactory.createSampleEntry(this, in, handlerType);
			if(entry!=null) {
				left -= entry.getSize();
				sampleEntries.add(entry);
			}
		}
	}

	public List<SampleEntry> getSampleEntries() {
		return sampleEntries;
	}
}
