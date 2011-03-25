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

import net.sourceforge.jaad.util.mp4.boxes.impl.MediaDataBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.AudioSampleEntryBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.ChunkOffsetBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.ESDBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.FileTypeBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.MediaHeaderBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.MovieHeaderBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.SampleSizeBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.SampleTableBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.SampleToChunkBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.TimeToSampleBox;
import net.sourceforge.jaad.util.mp4.boxes.impl.TrackHeaderBox;
import java.io.IOException;
import net.sourceforge.jaad.util.mp4.MP4InputStream;

public class BoxFactory implements BoxTypes {

	public static Box parseBox(Box parent, MP4InputStream in) throws IOException {
		long size = in.readBytes(4);
		long left = size-4;
		if(size==1) {
			size = in.readBytes(8);
			left -= 8;
		}
		long type = in.readBytes(4);
		left -= 4;
		if(type==EXTENDED_BOX) {
			type = in.readBytes(16);
			left -= 16;
		}

		final BoxImpl box = forType(type);
		box.setParams(size, type, parent, left);
		box.decode(in);
		//if mdat found, don't skip
		if(box.getType()!=MEDIA_DATA_BOX) in.skipBytes(box.getLeft());
		return box;
	}

	//TODO: this is ugly!
	private static BoxImpl forType(long type) {
		BoxImpl box;
		switch((int) type) {
			case MEDIA_DATA_BOX:
				box = new MediaDataBox();
				break;
			case FILE_TYPE_BOX:
				box = new FileTypeBox();
				break;
			case MOVIE_BOX:
			case TRACK_BOX:
			case MEDIA_BOX:
			case MEDIA_INFORMATION_BOX:
				box = new ContainerBoxImpl();
				break;
			case SOUND_MEDIA_HEADER_BOX:
				box = new FullBox();
				break;
			case MOVIE_HEADER_BOX:
				box = new MovieHeaderBox();
				break;
			case TRACK_HEADER_BOX:
				box = new TrackHeaderBox();
				break;
			case MEDIA_HEADER_BOX:
				box = new MediaHeaderBox();
				break;
			case SAMPLE_TABLE_BOX:
				box = new SampleTableBox();
				break;
			case SAMPLE_DESCRIPTION_BOX:
				box = new SampleDescriptionBox();
				break;
			case TIME_TO_SAMPLE_BOX:
				box = new TimeToSampleBox();
				break;
			case SAMPLE_SIZE_BOX:
				box = new SampleSizeBox();
				break;
			case SAMPLE_TO_CHUNK_BOX:
				box = new SampleToChunkBox();
				break;
			case ESD_BOX:
				box = new ESDBox();
				break;
			case AUDIO_SAMPLE_ENTRY_BOX:
				box = new AudioSampleEntryBox();
				break;
			case CHUNK_OFFSET_BOX:
			case CHUNK_LARGE_OFFSET_BOX:
				box = new ChunkOffsetBox();
				break;
			default:
				box = new UnknownBox();
		}
		return box;
	}
}
