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
