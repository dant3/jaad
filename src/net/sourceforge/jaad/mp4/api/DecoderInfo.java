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
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.*;

//TODO: find more common attributes or provide access to separate codec structs
public class DecoderInfo {

	private CodecSpecificBox css;

	public DecoderInfo(CodecSpecificBox css) {
		final long l = css.getType();
		if(l==BoxTypes.H263_SPECIFIC_BOX) {
			H263SpecificBox h263 = (H263SpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.AMR_SPECIFIC_BOX) {
			AMRSpecificBox amr = (AMRSpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.EVRC_SPECIFIC_BOX) {
			EVRCSpecificBox evc = (EVRCSpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.QCELP_SPECIFIC_BOX) {
			QCELPSpecificBox qc = (QCELPSpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.SMV_SPECIFIC_BOX) {
			SMVSpecificBox smv = (SMVSpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.AVC_SPECIFIC_BOX) {
			AVCSpecificBox avc = (AVCSpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.AC3_SPECIFIC_BOX) {
			AC3SpecificBox ac3 = (AC3SpecificBox) css;
			//TODO
		}
		else if(l==BoxTypes.EAC3_SPECIFIC_BOX) {
			EAC3SpecificBox eac3 = (EAC3SpecificBox) css;
			//TODO
		}
	}

	public int getDecoderVersion() {
		return css.getDecoderVersion();
	}

	public long getVendor() {
		return css.getVendor();
	}
}
