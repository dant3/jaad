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
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;

/**
 * The <code>CodecSpecificBox</code> can be used instead of an <code>ESDBox</code>
 * in a sample entry. It contains <code>DecoderSpecificInfo</code>s.
 *
 * @author in-somnia
 */
public class CodecSpecificBox extends BoxImpl {

	private CodecSpecificStructure struc;

	public CodecSpecificBox() {
		super("CodecSpecificBox");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		if(type==BoxTypes.H263_SPECIFIC_BOX) struc = new H263SpecificStructure();
		else if(type==BoxTypes.AMR_SPECIFIC_BOX) struc = new AMRSpecificStructure();
		else if(type==BoxTypes.EVRC_SPECIFIC_BOX) struc = new EVCRSpecificStructure();
		else if(type==BoxTypes.QCELP_SPECIFIC_BOX) struc = new QCELPSpecificStructure();
		else if(type==BoxTypes.SMV_SPECIFIC_BOX) struc = new SMVSpecificStructure();
		else if(type==BoxTypes.AVC_SPECIFIC_BOX) struc = new AVCSpecificStructure();
		else struc = new UnknownCodecSpecificStructure();

		struc.decode(in);
		left -= struc.getSize();
	}

	public CodecSpecificStructure getCodecSpecificStructure() {
		return struc;
	}
}
