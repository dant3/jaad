package net.sourceforge.jaad.mp4.boxes.impl.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class DecoderConfigDescriptor extends ObjectDescriptor {

	DecoderConfigDescriptor(int type, int size) {
		super(type, size);
	}

	void decode(MP4InputStream in) throws IOException {
		in.skipBytes(13);
		bytesRead += 13;
		
		readChildren(in);
	}
}
