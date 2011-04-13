package net.sourceforge.jaad.mp4.boxes.impl.meta;

import java.io.IOException;
import java.nio.charset.Charset;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

/**
 * This box contains the data for a metadata tag. It is right below an
 * iTunes metadata box (e.g. '@nam') or a custom meta tag box ('----'). A custom
 * meta tag box also contains a 'name'-box declaring the tag's name.
 *
 * @author in-somnia
 */
/*TODO: use generics here? -> each DataType should return <T> corresponding to
 its class (String/Integer/...)*/
public class ITunesMetadataBox extends FullBox {

	public enum DataType {

		IMPLICIT(/*Object.class*/),
		UTF8(/*String.class*/),
		UTF16(/*String.class*/),
		HTML(/*String.class*/),
		XML(/*String.class*/),
		UUID(/*Long.class*/),
		ISRC(/*String.class*/),
		MI3P(/*String.class*/),
		GIF(/*byte[].class*/),
		JPEG(/*byte[].class*/),
		PNG(/*byte[].class*/),
		URL(/*String.class*/),
		DURATION(/*Long.class*/),
		DATETIME(/*Long.class*/),
		GENRE(/*Integer.class*/),
		INTEGER(/*Long.class*/),
		RIAA(/*Integer.class*/),
		UPC(/*String.class*/),
		BMP(/*byte[].class*/),
		UNDEFINED(/*byte[].class*/);

		private DataType() {
		}

		private static DataType forInt(int i) {
			final DataType type;
			switch(i) {
				case 0:
					type = IMPLICIT;
					break;
				case 1:
					type = UTF8;
					break;
				case 2:
					type = UTF16;
					break;
				case 6:
					type = HTML;
					break;
				case 7:
					type = XML;
					break;
				case 8:
					type = UUID;
					break;
				case 9:
					type = ISRC;
					break;
				case 10:
					type = MI3P;
					break;
				case 12:
					type = GIF;
					break;
				case 13:
					type = JPEG;
					break;
				case 14:
					type = PNG;
					break;
				case 15:
					type = URL;
					break;
				case 16:
					type = DURATION;
					break;
				case 17:
					type = DATETIME;
					break;
				case 18:
					type = GENRE;
					break;
				case 21:
					type = INTEGER;
					break;
				case 24:
					type = RIAA;
					break;
				case 25:
					type = UPC;
					break;
				case 27:
					type = BMP;
					break;
				default:
					type = UNDEFINED;
			}
			return type;
		}
	}
	private DataType dataType;
	private byte[] data;

	public ITunesMetadataBox() {
		super("iTunes Metadata Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		dataType = DataType.forInt(flags);

		data = new byte[(int) left];
		in.readBytes(data);
		left = 0;
	}

	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Returns the raw content, that can be present in different formats.
	 * @return the raw metadata
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns the content as a text string.
	 * @return the metadata as text
	 */
	public String getText() {
		//first four bytes are padding (zero)
		return new String(data, 4, data.length-4, Charset.forName("UTF-8"));
	}

	/**
	 * Returns the content as an unsigned 8-bit integer.
	 * @return the metadata as an integer
	 */
	public long getNumber() {
		//first four bytes are padding (zero)
		long l = 0;
		for(int i = 5; i<data.length; i++) {
			l <<= 8;
			l |= data[i];
		}
		return l;
	}

	/**
	 * Returns the content as a boolean (flag) value.
	 * @return the metadata as a boolean
	 */
	public boolean getBoolean() {
		return getNumber()!=0;
	}
}
