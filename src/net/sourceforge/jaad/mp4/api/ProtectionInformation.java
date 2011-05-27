package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.api.Track.Codec;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.OriginalFormatBox;
import net.sourceforge.jaad.mp4.boxes.impl.SchemeTypeBox;

/**
 * This class contains information about a DRM system.
 */
public class ProtectionInformation {

	private final Codec originalFormat;
	private final Scheme scheme;

	public enum Scheme {

		OMA_DRM("Open Mobile Alliance DRM", 1868852077),
		ITUNES_DRM("iTunes DRM", 1769239918),
		UNKNOWN_SCHEME("Unknown Protection Scheme", 0);
		private String name;
		private long type;

		private Scheme(String name, long type) {
			this.name = name;
			this.type = type;
		}

		private static Scheme forType(long type) {
			Scheme scheme = null;
			final Scheme[] all = values();
			for(int i = 0; scheme==null&&i<all.length; i++) {
				if(all[i].type==type) scheme = all[i];
			}
			if(scheme==null) scheme = UNKNOWN_SCHEME;
			return scheme;
		}
	}

	ProtectionInformation(Box sinf) {
		//original format
		final long type = ((OriginalFormatBox) sinf.getChild(BoxTypes.ORIGINAL_FORMAT_BOX)).getOriginalFormat();
		Codec c;
		//TODO: currently it tests for audio and video codec, can do this any other way?
		if(!(c = AudioTrack.AudioCodec.forType(type)).equals(AudioTrack.AudioCodec.UNKNOWN_AUDIO_CODEC)) originalFormat = c;
		else if(!(c = VideoTrack.VideoCodec.forType(type)).equals(VideoTrack.VideoCodec.UNKNOWN_VIDEO_CODEC)) originalFormat = c;
		else originalFormat = null;

		//scheme
		long l = 0;
		if(sinf.hasChild(BoxTypes.SCHEME_TYPE_BOX)) {
			SchemeTypeBox schm = (SchemeTypeBox) sinf.getChild(BoxTypes.SCHEME_TYPE_BOX);
			l = schm.getSchemeType();
		}
		scheme = Scheme.forType(l);
	}

	public Codec getOriginalFormat() {
		return originalFormat;
	}

	public Scheme getScheme() {
		return scheme;
	}
}
