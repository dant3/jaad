/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	public static final long ITUNES_SCHEME = 1769239918; //itun
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
