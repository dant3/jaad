package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class ID3TagBox extends FullBox {

	private String language;
	private byte[] id3Data;

	public ID3TagBox() {
		super("ID3 Tag Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		//1 bit padding, 5*3 bits language code (ISO-639-2/T)
		final long l = in.readBytes(2);
		char[] c = new char[3];
		c[0] = (char) (((l>>10)&31)+0x60);
		c[1] = (char) (((l>>5)&31)+0x60);
		c[2] = (char) ((l&31)+0x60);
		language = new String(c);
		left -= 2;

		id3Data = new byte[(int) left];
		in.readBytes(id3Data);
		left = 0;
	}

	public String getLanguage() {
		return language;
	}

	public byte[] getID3Data() {
		return id3Data;
	}
}
