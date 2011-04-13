package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox.DataType;

public class Artwork {

	//TODO: need this enum? it just copies the DataType
	public enum Type {

		GIF, JPEG, PNG, BMP;

		static Type forDataType(DataType dataType) {
			Type type;
			switch(dataType) {
				case GIF:
					type = GIF;
					break;
				case JPEG:
					type = JPEG;
					break;
				case PNG:
					type = PNG;
					break;
				case BMP:
					type = BMP;
					break;
				default:
					type = null;
			}
			return type;
		}
	}
	private Type type;
	private byte[] data;

	Artwork(Type type, byte[] data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Returns the type of data in this artwork.
	 *
	 * @see Type
	 * @return the data's type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns this artwork's data.
	 *
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
}
