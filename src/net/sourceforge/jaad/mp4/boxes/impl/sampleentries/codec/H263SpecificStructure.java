package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class H263SpecificStructure extends CodecSpecificStructure {

	private int level, profile;

	H263SpecificStructure() {
		super(7);
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		level = in.read();
		profile = in.read();
	}

	public int getLevel() {
		return level;
	}

	public int getProfile() {
		return profile;
	}
}
