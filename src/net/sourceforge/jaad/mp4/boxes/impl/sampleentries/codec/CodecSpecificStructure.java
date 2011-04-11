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

	abstract void decode(MP4InputStream in) throws IOException;
}
