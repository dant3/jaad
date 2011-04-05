package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class ITunesMetadataBox extends FullBox {

	public ITunesMetadataBox() {
		super("iTunes Metadata Box", "data");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		//TODO
	}
}
