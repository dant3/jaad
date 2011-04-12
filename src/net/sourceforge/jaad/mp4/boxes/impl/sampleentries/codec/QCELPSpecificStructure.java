package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

class QCELPSpecificStructure extends CodecSpecificStructure {

	private int framesPerSample;

	QCELPSpecificStructure() {
		super(6);
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		framesPerSample = in.read();
	}

	public int getFramesPerSample() {
		return framesPerSample;
	}
}
