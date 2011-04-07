package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class ID3TagBox extends FullBox {

	public ID3TagBox() {
		super("ID3 Tag Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		//TODO
	}
}
