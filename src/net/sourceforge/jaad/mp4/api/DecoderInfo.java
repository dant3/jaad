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
package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificStructure;

public class DecoderInfo {

	private long vendor;
	private int decoderVersion, level, profile;

	public DecoderInfo(CodecSpecificBox css) {
		final long l = css.getType();
		if(l==BoxTypes.H263_SPECIFIC_BOX) {
			H263SpecificStructure h263 = (H263SpecificStructure) css.getCodecSpecificStructure();
			vendor = h263.getVendor();
			decoderVersion = h263.getDecoderVersion();
			level = h263.getLevel();
			profile = h263.getProfile();
		}
	}

	public int getDecoderVersion() {
		return decoderVersion;
	}

	public int getLevel() {
		return level;
	}

	public int getProfile() {
		return profile;
	}

	public long getVendor() {
		return vendor;
	}
}
