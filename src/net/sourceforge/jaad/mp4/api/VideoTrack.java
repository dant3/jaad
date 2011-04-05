package net.sourceforge.jaad.mp4.api;

import java.awt.Color;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.mp4.boxes.impl.VideoMediaHeaderBox;

public class VideoTrack extends Track {

	private VideoMediaHeaderBox vmhd;

	public VideoTrack(Box trak) {
		super(trak);

		final ContainerBox mdia = (ContainerBox) ((ContainerBox) trak).getChild(BoxTypes.MEDIA_BOX);
		final ContainerBox minf = (ContainerBox) mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX);
		vmhd = (VideoMediaHeaderBox) minf.getChild(BoxTypes.VIDEO_MEDIA_HEADER_BOX);
	}

	@Override
	public Type getType() {
		return Type.VIDEO;
	}
}
