package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class AMRSpecificStructure extends CodecSpecificStructure {

	private int modeSet, modeChangePeriod, framesPerSample;

	AMRSpecificStructure() {
		super(9);
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		modeSet = (int) in.readBytes(2);
		modeChangePeriod = in.read();
		framesPerSample = in.read();
	}

	public int getModeSet() {
		return modeSet;
	}

	public int getModeChangePeriod() {
		return modeChangePeriod;
	}

	public int getFramesPerSample() {
		return framesPerSample;
	}
}
