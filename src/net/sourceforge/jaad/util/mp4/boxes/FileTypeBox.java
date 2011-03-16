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
package net.sourceforge.jaad.util.mp4.boxes;

import net.sourceforge.jaad.util.mp4.BoxImpl;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import java.io.IOException;

public class FileTypeBox extends BoxImpl {

	protected long majorBrand, minorVersion;
	protected long[] compatibleBrands;

	@Override
	public void decode(MP4InputStream in) throws IOException {
		majorBrand = in.readBytes(4);
		minorVersion = in.readBytes(4);
		left -= 8;
		compatibleBrands = new long[(int) left/4];
		for(int i = 0; i<compatibleBrands.length; i++) {
			compatibleBrands[i] = in.readBytes(4);
			left -= 4;
		}
	}
}
