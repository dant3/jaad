package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

/**
 * The <code>CodecSpecificStructure</code> contains specific information for the
 * decoder. It is present in a <code>CodecSpecificBox</code>.
 *
 * @author in-somnia
 */
public abstract class CodecSpecificStructure {

	protected int size;
	private long vendor;
	private int decoderVersion;

	protected CodecSpecificStructure(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	void decode(MP4InputStream in) throws IOException {
		vendor = in.readBytes(4);
		decoderVersion = in.read();
	}

	public long getVendor() {
		return vendor;
	}

	public int getDecoderVersion() {
		return decoderVersion;
	}
}
