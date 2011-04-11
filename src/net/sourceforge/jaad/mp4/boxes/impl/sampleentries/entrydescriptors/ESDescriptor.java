package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.entrydescriptors;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class ESDescriptor extends ObjectDescriptor {

	ESDescriptor(int type, int size) {
		super(type, size);
	}

	void decode(MP4InputStream in) throws IOException {
		in.skipBytes(2);
		final int flags = in.read();
		final boolean streamDependenceFlag = (flags&(1<<7))!=0;
		final boolean urlFlag = (flags&(1<<6))!=0;
		final boolean ocrFlag = (flags&(1<<5))!=0;
		bytesRead += 3;
		if(streamDependenceFlag) {
			in.skipBytes(2);
			bytesRead += 2;
		}
		if(urlFlag) {
			final int len = in.read();
			in.skipBytes(len);
			bytesRead += len+1;
		}
		if(ocrFlag) {
			in.skipBytes(2);
			bytesRead += 2;
		}

		readChildren(in);
	}
}
