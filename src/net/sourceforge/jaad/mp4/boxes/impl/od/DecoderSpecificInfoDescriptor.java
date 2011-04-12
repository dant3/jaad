package net.sourceforge.jaad.mp4.boxes.impl.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class DecoderSpecificInfoDescriptor extends ObjectDescriptor {

	private byte[] data;

	public DecoderSpecificInfoDescriptor(int type, int size) {
		super(type, size);
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
		data = new byte[size];
		in.readBytes(data);
		bytesRead += size;
	}

	public byte[] getData() {
		return data;
	}
}
