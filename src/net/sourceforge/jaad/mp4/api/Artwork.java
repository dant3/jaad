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

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox.DataType;

public class Artwork {

	public static void main(String[] args) {
		try {
			MP4Container cont = new MP4Container(new RandomAccessFile("C:\\artwork.mp4", "r"));
			Movie movie = cont.getMovie();
			MetaData meta = movie.getMetaData();
			List<Artwork> art = meta.get(MetaData.Field.COVER_ARTWORK);
			System.out.println("len: "+art.size());
			Image im = art.get(0).getImage();
			System.out.println("im: "+im);
			JFrame frame = new JFrame();
			frame.setSize(im.getWidth(frame)+50, im.getHeight(frame)+50);
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add("Center", new JLabel(new ImageIcon(im)));
			frame.setVisible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
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
	private Image image;

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
	 * Returns the encoded data of this artwork.
	 *
	 * @return the encoded data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns the decoded image, that can be painted.
	 *
	 * @return the decoded image
	 * @throws IOException if decoding fails
	 */
	public Image getImage() throws IOException {
		if(image==null) image = ImageIO.read(new ByteArrayInputStream(data));
		return image;
	}
}
