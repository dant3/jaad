package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import java.nio.charset.Charset;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * This box contains the data for a metadata tag. It is right below an
 * iTunes metadata box (e.g. '@nam') or a custom meta tag box ('----'). A custom
 * meta tag box also contains a 'name'-box declaring the tag's name.
 *
 * @author in-somnia
 */
public class ITunesMetadataBox extends FullBox {

	private byte[] data;

	public ITunesMetadataBox() {
		super("iTunes Metadata Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		data = new byte[(int) left];
		in.readBytes(data);
		left = 0;
	}

	/**
	 * Returns the raw content, that can be present in different formats.
	 * @return the raw metadata
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns the content as a text string.
	 * @return the metadata as text
	 */
	public String getText() {
		//first four bytes are padding (zero)
		return new String(data, 4, data.length-4, Charset.forName("UTF-8"));
	}
}
