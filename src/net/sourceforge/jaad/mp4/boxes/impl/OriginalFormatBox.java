package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * The Original Format Box contains the four-character-code of the original
 * un-transformed sample description.
 *
 * @author in-somnia
 */
public class OriginalFormatBox extends FullBox {

	private long originalFormat;

	public OriginalFormatBox() {
		super("Original Format Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		originalFormat = in.readBytes(4);
		left -= 4;
	}

	/**
	 * The original format is the four-character-code of the original
	 * un-transformed sample entry (e.g. 'mp4v' if the stream contains protected
	 * MPEG-4 visual material).
	 *
	 * @return the stream's original format
	 */
	public long getOriginalFormat() {
		return originalFormat;
	}
}
