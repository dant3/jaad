/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTYll; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad.mp4.boxes.impl.oma;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class OMACommonHeadersBox extends FullBox {

	private int encryptionMethod, paddingScheme;
	private long plaintextLength;
	private int contentIDLength, rightsIssuerURLLength, textualHeadersLength;
	private byte[] contentID, rightsIssuerURL, textualHeaders;

	public OMACommonHeadersBox() {
		super("OMA DMR Common Header Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		encryptionMethod = in.read();
		paddingScheme = in.read();
		plaintextLength = in.readBytes(8);
		contentIDLength = (int) in.readBytes(2);
		rightsIssuerURLLength = (int) in.readBytes(2);
		textualHeadersLength = (int) in.readBytes(2);
		contentID = new byte[contentIDLength];
		in.readBytes(contentID);
		rightsIssuerURL = new byte[rightsIssuerURLLength];
		in.readBytes(rightsIssuerURL);
		textualHeaders = new byte[textualHeadersLength];
		in.readBytes(textualHeaders);

		readChildren(in);
	}

	public int getEncryptionMethod() {
		return encryptionMethod;
	}

	public int getPaddingScheme() {
		return paddingScheme;
	}

	public long getPlaintextLength() {
		return plaintextLength;
	}

	public byte[] getContentID() {
		return contentID;
	}

	public byte[] getRightsIssuerURL() {
		return rightsIssuerURL;
	}

	public byte[] getTextualHeaders() {
		return textualHeaders;
	}
}
