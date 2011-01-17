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
package jaad.util.mp4;

import jaad.util.mp4.boxes.MediaDataBox;
import jaad.util.mp4.boxes.AudioSampleEntryBox;
import jaad.util.mp4.boxes.ChunkOffsetBox;
import jaad.util.mp4.boxes.ESDBox;
import jaad.util.mp4.boxes.FileTypeBox;
import jaad.util.mp4.boxes.MediaHeaderBox;
import jaad.util.mp4.boxes.MovieHeaderBox;
import jaad.util.mp4.boxes.SampleDescriptionBox;
import jaad.util.mp4.boxes.SampleSizeBox;
import jaad.util.mp4.boxes.SampleTableBox;
import jaad.util.mp4.boxes.SampleToChunkBox;
import jaad.util.mp4.boxes.TimeToSampleBox;
import jaad.util.mp4.boxes.TrackHeaderBox;
import java.io.IOException;

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
