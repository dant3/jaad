package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * The Scheme Type Box identifies the protection scheme.
 * 
 * @author in-somnia
 */
public class SchemeTypeBox extends FullBox {

	private long schemeType, schemeVersion;
	private String schemeURI;

	public SchemeTypeBox() {
		super("Scheme Type Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		schemeType = in.readBytes(4);
		schemeVersion = in.readBytes(4);
		left -= 8;

		if((flags&1)==1) {
			schemeURI = in.readUTFString((int) left, MP4InputStream.UTF8);
			left -= schemeURI.length()+1;
		}
		else schemeURI = null;
	}

	/**
	 * The scheme type is the code defining the protection scheme.
	 *
	 * @return the scheme type
	 */
	public long getSchemeType() {
		return schemeType;
	}

	/**
	 * The scheme version is the version of the scheme used to create the
	 * content.
	 *
	 * @return the scheme version
	 */
	public long getSchemeVersion() {
		return schemeVersion;
	}

	/**
	 * The optional scheme URI allows for the option of directing the user to a
	 * web-page if they do not have the scheme installed on their system. It is
	 * an absolute URI.
	 * If the scheme URI is not present, this method returns null.
	 *
	 * @return the scheme URI or null, if no URI is present
	 */
	public String getSchemeURI() {
		return schemeURI;
	}
}
