package net.sourceforge.jaad.mp4.boxes.impl.od;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

public class UnknownDescriptor extends ObjectDescriptor {

	public UnknownDescriptor(int type, int size) {
		super(type, size);
	}

	@Override
	void decode(MP4InputStream in) throws IOException {
	}
}
