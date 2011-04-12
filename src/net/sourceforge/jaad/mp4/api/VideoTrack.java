package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.mp4.boxes.impl.VideoMediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.descriptors.ESDBox;

public class VideoTrack extends Track {

	private VideoMediaHeaderBox vmhd;
	private VideoSampleEntry sampleEntry;

	public VideoTrack(Box trak, MP4InputStream in) {
		super(trak, in);

		final Box minf = trak.getChild(BoxTypes.MEDIA_BOX).getChild(BoxTypes.MEDIA_INFORMATION_BOX);
		vmhd = (VideoMediaHeaderBox) minf.getChild(BoxTypes.VIDEO_MEDIA_HEADER_BOX);

		final Box stbl = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX);

		//sample descriptions
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX);
		sampleEntry = (VideoSampleEntry) stsd.getChild(BoxTypes.AUDIO_SAMPLE_ENTRY);
		if(sampleEntry.getType()==VideoSampleEntry.TYPE_MP4V) {
			findDecoderSpecificInfo((ESDBox) sampleEntry.getChild(BoxTypes.ESD_BOX));
		}
		else {
			final CodecSpecificBox csb = (CodecSpecificBox) sampleEntry.getChildren().get(0);
			codecSpecificStructure = csb.getCodecSpecificStructure();
		}
	}

	@Override
	public Type getType() {
		return Type.VIDEO;
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
