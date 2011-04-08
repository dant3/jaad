package net.sourceforge.jaad.mp4.api;

public class Artwork {

	public enum Type {

		JPEG, PNG;

		static Type forInt(int i) {
			Type type;
			switch(i) {
				case 13:
					type = JPEG;
					break;
				case 14:
					type = PNG;
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
