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
package net.sourceforge.jaad.util.mp4.boxes.impl;

import net.sourceforge.jaad.util.mp4.boxes.BoxImpl;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import java.io.IOException;

public class FileTypeBox extends BoxImpl {

	public static final String BRAND_MP41 = "mp41";
	public static final String BRAND_ISOM = "isom";
	public static final String BRAND_ISO2 = "iso2";
	public static final String BRAND_ISO3 = "iso3";
	public static final String BRAND_AVC1 = "avc1";
	public static final String BRAND_MP71 = "mp71";
	protected String majorBrand, minorVersion;
	protected String[] compatibleBrands;

	@Override
	public void decode(MP4InputStream in) throws IOException {
		majorBrand = in.readString(4);
		minorVersion = in.readString(4);
		left -= 8;
		compatibleBrands = new String[(int) left/4];
		for(int i = 0; i<compatibleBrands.length; i++) {
			compatibleBrands[i] = in.readString(4);
			left -= 4;
		}
	}

	public String getMajorBrand() {
		return majorBrand;
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public String[] getCompatibleBrands() {
		return compatibleBrands;
	}
}
