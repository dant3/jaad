package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.boxes.Utils;

public class ThreeGPPMetadataBox extends FullBox {

	private String languageCode, data;

	public ThreeGPPMetadataBox() {
		super("3GPP Metadata Box");
	}

	protected ThreeGPPMetadataBox(String name) {
		super(name);
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		decodeCommon(in);

		data = in.readUTFString((int) left);
		left -= data.length()+1;
	}

	//called directly by subboxes that don't contain the 'data' string
	protected void decodeCommon(MP4InputStream in) throws IOException {
		super.decode(in);
		languageCode = Utils.getLanguageCode(in.readBytes(2));
		left -= 2;
	}

	/**
	 * The language code for the following text. See ISO 639-2/T for the set of
	 * three character codes.
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	public String getData() {
		return data;
	}
}
