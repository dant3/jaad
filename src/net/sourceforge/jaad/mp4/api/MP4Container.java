package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.mp4.boxes.impl.FileTypeBox;
import net.sourceforge.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox;

/**
 * The MP4Container is the top-level class for the MP4 API. It can contain a
 * Movie and information about the branch and the playback delay while 
 * downloading.
 * 
 * @author in-somnia
 */
public class MP4Container {

	private FileTypeBox ftyp;
	private ProgressiveDownloadInformationBox pdin;
	private Movie movie;

	public MP4Container(Box[] topLevelBoxes) {
		for(Box box : topLevelBoxes) {
			final long l = box.getType();
			if(l==BoxTypes.FILE_TYPE_BOX&&ftyp==null) ftyp = (FileTypeBox) box;
			else if(l==BoxTypes.PROGRESSIVE_DOWNLOAD_INFORMATION_BOX&&pdin==null) pdin = (ProgressiveDownloadInformationBox) box;
			else if(l==BoxTypes.MOVIE_BOX&&movie==null) movie = new Movie((ContainerBox) box);
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

	public Movie getMovie() {
		return movie;
	}
}
