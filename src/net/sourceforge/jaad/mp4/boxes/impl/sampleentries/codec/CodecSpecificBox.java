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

	public CodecSpecificBox(String name) {
		super("CodecSpecificBox");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		if(type==BoxTypes.H263_SPECIFIC_BOX) struc = new H263SpecificStructure();
		//else if...
		else struc = new UnknownCodecSpecificStructure();

		struc.decode(in);
	}

	public CodecSpecificStructure getCodecSpecificStructure() {
		return struc;
	}
}
