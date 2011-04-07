package net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public abstract class SampleEntry extends BoxImpl {

	private long dataReferenceIndex;

	protected SampleEntry(String name) {
		super(name);
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		//6*8 bits reserved
		in.skipBytes(6);
		dataReferenceIndex = in.readBytes(2);
		left -= 8;
	}

	/**
	 * The data reference index is an integer that contains the index of the
	 * data reference to use to retrieve data associated with samples that use
	 * this sample description. Data references are stored in Data Reference
	 * Boxes. The index ranges from 1 to the number of data references.
	 */
	public long getDataReferenceIndex() {
		return dataReferenceIndex;
	}
}
