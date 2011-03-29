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

import net.sourceforge.jaad.util.mp4.boxes.ContainerBoxImpl;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import java.io.IOException;

/**
 * The sample table contains all the time and data indexing of the media samples
 * in a track. Using the tables here, it is possible to locate samples in time,
 * determine their type (e.g. I-frame or not), and determine their size,
 * container, and offset into that container.
 *
 * If the track that contains the Sample Table Box references no data, then the
 * Sample Table Box does not need to contain any sub-boxes (this is not a very
 * useful media track).
 *
 * If the track that the Sample Table Box is contained in does reference data,
 * then the following sub-boxes are required: Sample Description, Sample Size,
 * Sample To Chunk, and Chunk Offset. Further, the Sample Description Box shall
 * contain at least one entry. A Sample Description Box is required because it
 * contains the data reference index field which indicates which Data Reference
 * Box to use to retrieve the media samples. Without the Sample Description, it
 * is not possible to determine where the media samples are stored. The Sync
 * Sample Box is optional. If the Sync Sample Box is not present, all samples
 * are sync samples.
 *
 * @author in-somnia
 */
public class SampleTableBox extends ContainerBoxImpl {

	private boolean sound = false;

	public SampleTableBox() {
		super("Sample Table Box", "stbl");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
	}

	//TODO: not good, find another way!
	public boolean isSound() {
		return sound;
	}
}
