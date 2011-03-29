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

import net.sourceforge.jaad.util.mp4.boxes.impl.sampleentries.*;
import java.io.IOException;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import net.sourceforge.jaad.util.mp4.boxes.impl.*;

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

		//DEBUG
		System.out.println(box.getShortName());
		//

		box.setParams(size, type, parent, left);
		box.decode(in);
		//if mdat found, don't skip
		if(box.getType()!=MEDIA_DATA_BOX) in.skipBytes(box.getLeft());
		return box;
	}

	public static SampleEntry createSampleEntry(Box parent, MP4InputStream in, int handlerType) throws IOException {
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

		SampleEntry entry;
		switch(handlerType) {
			case HandlerBox.TYPE_VIDEO:
				entry = new VideoSampleEntry();
				break;
			case HandlerBox.TYPE_SOUND:
				entry = new AudioSampleEntry();
				break;
			case HandlerBox.TYPE_HINT:
				entry = new HintSampleEntry();
				break;
			case HandlerBox.TYPE_META:
				if(type==TEXT_METADATA_SAMPLE_ENTRY) entry = new TextMetadataSampleEntry();
				else if(type==XML_METADATA_SAMPLE_ENTRY) entry = new XMLMetadataSampleEntry();
				else entry = null;
				break;
			default:
				entry = null;
				break;
		}

		if(entry!=null) {
			//DEBUG
			System.out.println(entry.getShortName());
			//
			entry.setParams(size, type, parent, left);
			entry.decode(in);
		}
		return entry;
	}

	//TODO: this is ugly!
	private static BoxImpl forType(long type) {
		BoxImpl box;
		switch((int) type) {
			//top level
			case FILE_TYPE_BOX:
				box = new FileTypeBox();
				break;
			case MEDIA_DATA_BOX:
				box = new MediaDataBox();
				break;
			//only container
			case MOVIE_BOX:
				box = new ContainerBoxImpl("Movie Box", "moov");
				break;
			case TRACK_BOX:
				box = new ContainerBoxImpl("Track Box", "trak");
				break;
			case MEDIA_BOX:
				box = new ContainerBoxImpl("Media Box", "mdia");
				break;
			case MEDIA_INFORMATION_BOX:
				box = new ContainerBoxImpl("Media Information Box", "minf");
				break;
			case DATA_INFORMATION_BOX:
				box = new ContainerBoxImpl("Data Information Box", "dinf");
				break;
			case MOVIE_EXTENDS_BOX:
				box = new ContainerBoxImpl("Movie Extends Box", "mvex");
				break;
			//
			case HANDLER_BOX:
				box = new HandlerBox();
				break;
			//media header
			case VIDEO_MEDIA_HEADER_BOX:
				box = new VideoMediaHeaderBox();
				break;
			case SOUND_MEDIA_HEADER_BOX:
				box = new SoundMediaHeaderBox();
				break;
			case HINT_MEDIA_HEADER_BOX:
				box = new HintMediaHeaderBox();
				break;
			case NULL_MEDIA_HEADER_BOX:
				box = new FullBox("Null Media Header Box", "nmhd");
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
			//sample table
			case SAMPLE_TABLE_BOX:
				box = new SampleTableBox();
				break;
			case SAMPLE_DESCRIPTION_BOX:
				box = new SampleDescriptionBox();
				break;
			case BIT_RATE_BOX:
				box = new BitRateBox();
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
			case CHUNK_OFFSET_BOX:
			case CHUNK_LARGE_OFFSET_BOX:
				box = new ChunkOffsetBox();
				break;
			case PADDING_BIT_BOX:
				box = new PaddingBitBox();
				break;
			//track data layout boxes
			case DATA_ENTRY_URL_BOX:
				box = new DataEntryUrlBox();
				break;
			case DATA_ENTRY_URN_BOX:
				box = new DataEntryUrnBox();
				break;
			case DATA_REFERENCE_BOX:
				box = new DataReferenceBox();
				break;
			case DEGRADATION_PRIORITY_BOX:
				box = new DegradationPriorityBox();
				break;
			//movie fragments
			case MOVIE_EXTENDS_HEADER_BOX:
				box = new MovieExtendsHeaderBox();
				break;
			default:
				box = new UnknownBox();
		}
		return box;
	}
}
