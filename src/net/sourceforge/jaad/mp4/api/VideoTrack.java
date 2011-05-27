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
package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.mp4.boxes.impl.VideoMediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import net.sourceforge.jaad.mp4.boxes.impl.ESDBox;

public class VideoTrack extends Track {

	public enum VideoCodec implements Codec {

		AVC,
		H263,
		MP4_ASP,
		UNKNOWN_VIDEO_CODEC;

		static Codec forType(long type) {
			final Codec ac;
			if(type==BoxTypes.AVC_SAMPLE_ENTRY) ac = AVC;
			else if(type==BoxTypes.H263_SAMPLE_ENTRY) ac = H263;
			else if(type==BoxTypes.MP4V_SAMPLE_ENTRY) ac = MP4_ASP;
			else ac = UNKNOWN_VIDEO_CODEC;
			return ac;
		}
	}
	private final VideoMediaHeaderBox vmhd;
	private final VideoSampleEntry sampleEntry;
	private final Codec codec;

	public VideoTrack(Box trak, MP4InputStream in) {
		super(trak, in);

		final Box minf = trak.getChild(BoxTypes.MEDIA_BOX).getChild(BoxTypes.MEDIA_INFORMATION_BOX);
		vmhd = (VideoMediaHeaderBox) minf.getChild(BoxTypes.VIDEO_MEDIA_HEADER_BOX);

		final Box stbl = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX);

		//sample descriptions: 'mp4v' has an ESDBox, all others have a CodecSpecificBox
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX);
		sampleEntry = (VideoSampleEntry) stsd.getChildren().get(0);
		final long type = sampleEntry.getType();
		if(type==BoxTypes.MP4V_SAMPLE_ENTRY) findDecoderSpecificInfo((ESDBox) sampleEntry.getChild(BoxTypes.ESD_BOX));
		else if(type==BoxTypes.ENCRYPTED_VIDEO_SAMPLE_ENTRY) {
			findDecoderSpecificInfo((ESDBox) sampleEntry.getChild(BoxTypes.ESD_BOX));
			protection = new ProtectionInformation(sampleEntry.getChild(BoxTypes.PROTECTION_SCHEME_INFORMATION_BOX));
		}
		else decoderInfo = new DecoderInfo((CodecSpecificBox) sampleEntry.getChildren().get(0));

		codec = VideoCodec.forType(sampleEntry.getType());
	}

	@Override
	public Type getType() {
		return Type.VIDEO;
	}

	@Override
	public Codec getCodec() {
		return codec;
	}

	public int getWidth() {
		return sampleEntry.getWidth();
	}

	public int getHeight() {
		return sampleEntry.getHeight();
	}

	public double getHorizontalResolution() {
		return sampleEntry.getHorizontalResolution();
	}

	public double getVerticalResolution() {
		return sampleEntry.getVerticalResolution();
	}

	public int getFrameCount() {
		return sampleEntry.getFrameCount();
	}

	public String getCompressorName() {
		return sampleEntry.getCompressorName();
	}

	public int getDepth() {
		return sampleEntry.getDepth();
	}

	public int getLayer() {
		return tkhd.getLayer();
	}
}
