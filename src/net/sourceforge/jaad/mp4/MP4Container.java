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
package net.sourceforge.jaad.mp4;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxFactory;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.FileTypeBox;
import net.sourceforge.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox;

/**
 * The MP4Container is the central class for the MP4 demultiplexer. It reads the
 * container and gives access to the boxes and the containing data.
 *
 * The data source can be either an <code>InputStream</code> or a
 * <code>RandomAccessFile</code>. Since the specification does not decree a
 * specific order of the boxes, the data needed for parsing (e.g. the sample
 * tables) may be at the end of the stream. In this case, random access is
 * needed and reading from an <code>InputSteam</code> will cause an exception.
 *
 * Whenever possible, a <code>RandomAccessFile</code> should be used for local
 * files. Parsing from an <code>InputStream</code> is useful when reading from
 * a network stream.
 *
 * Each <code>MP4Container</code> can return the used file brand (file format
 * version). Optionally, the following data may be present:
 * <ul>
 * <li>progressive download informations: pairs of download rate and playback
 * delay, see {@link #<getDownloadInformationPairs()> [getDownloadInformationPairs()]}</li>
 * <li>a <code>Movie</code></li>
 * </ul>
 *
 * Finally it gives access to the underlying MP4 boxes, that can be retrieved
 * by <code>getBoxes()</code>.
 * 
 * @author in-somnia
 */
public class MP4Container {

	private final MP4InputStream in;
	private final List<Box> boxes;
	private FileTypeBox ftyp;
	private ProgressiveDownloadInformationBox pdin;
	private Box moov;
	private Movie movie;

	public MP4Container(InputStream in) throws IOException {
		this.in = new MP4InputStream(in);
		boxes = new ArrayList<Box>();

		readContent();
	}

	public MP4Container(RandomAccessFile in) throws IOException {
		this.in = new MP4InputStream(in);
		boxes = new ArrayList<Box>();
	}

	private void readContent() throws IOException {
		//read all boxes
		Box box = null;
		long type;
		boolean moovFound = false;
		//TODO: while(true)???
		while(true) {
			box = BoxFactory.parseBox(null, in);
			boxes.add(box);

			type = box.getType();
			if(type==BoxTypes.MOVIE_BOX) {
				if(movie==null) moov = box;
				moovFound = true;
			}
			else if(type==BoxTypes.FILE_TYPE_BOX) {
				if(ftyp==null) ftyp = (FileTypeBox) box;
			}
			else if(type==BoxTypes.PROGRESSIVE_DOWNLOAD_INFORMATION_BOX) {
				if(pdin==null) pdin = (ProgressiveDownloadInformationBox) box;
			}
			else if(type==BoxTypes.MEDIA_DATA_BOX) {
				if(moovFound) break;
				else if(!in.hasRandomAccess()) throw new MP4Exception("movie box at end of file, need random access");
			}
		}
	}

	public String getMajorBrand() {
		return ftyp.getMajorBrand();
	}

	public String getMinorBrand() {
		return ftyp.getMajorBrand();
	}

	public String[] getCompatibleBrands() {
		return ftyp.getCompatibleBrands();
	}

	//TODO: pdin, movie fragments??
	public Movie getMovie() {
		if(movie==null) movie = new Movie(moov, in);
		return movie;
	}

	public List<Box> getBoxes() {
		return Collections.unmodifiableList(boxes);
	}
}
