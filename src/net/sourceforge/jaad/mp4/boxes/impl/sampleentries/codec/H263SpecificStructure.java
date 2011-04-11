package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class H263SpecificStructure extends  CodecSpecificStructure {

	private long vendor;
	private int decoderVersion, level, profile;

	H263SpecificStructure() {
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
		vendor = in.readBytes(4);
		decoderVersion = in.read();
		level = in.read();
		profile = in.read();
	}

	public long getVendor() {
		return vendor;
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
}
