package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificStructure;

public class DecoderInfo {

	private long vendor;
	private int decoderVersion, level, profile;

	public DecoderInfo(CodecSpecificBox css) {
		final long l = css.getType();
		if(l==BoxTypes.H263_SPECIFIC_BOX) {
			H263SpecificStructure h263 = (H263SpecificStructure) css.getCodecSpecificStructure();
			vendor = h263.getVendor();
			decoderVersion = h263.getDecoderVersion();
			level = h263.getLevel();
			profile = h263.getProfile();
		}
	}

	public int getDecoderVersion() {
		return decoderVersion;
	}

	public int getLevel() {
		return level;
	}

	public int getProfile() {
		return profile;
	}

	public long getVendor() {
		return vendor;
	}
}
