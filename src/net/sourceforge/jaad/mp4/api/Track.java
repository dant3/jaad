package net.sourceforge.jaad.mp4.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrlBox;
import net.sourceforge.jaad.mp4.boxes.impl.DataReferenceBox;
import net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.mp4.boxes.impl.TrackHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;

public abstract class Track {

	public static enum Type {

		VIDEO,
		AUDIO
	}
	private Type type;
	private final TrackHeaderBox tkhd;
	private final MediaHeaderBox mdhd;
	private final boolean inFile;
	private URL location;

	Track(Box trackBox) {
		final ContainerBox cb = (ContainerBox) trackBox;

		tkhd = (TrackHeaderBox) cb.getChild(BoxTypes.TRACK_HEADER_BOX);

		//mdia
		final ContainerBox mdia = (ContainerBox) cb.getChild(BoxTypes.MEDIA_BOX);
		mdhd = (MediaHeaderBox) mdia.getChild(BoxTypes.MEDIA_HEADER_BOX);
		final ContainerBox minf = (ContainerBox) mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX);

		//dinf
		final ContainerBox dinf = (ContainerBox) minf.getChild(BoxTypes.DATA_INFORMATION_BOX);
		final DataReferenceBox dref = (DataReferenceBox) dinf.getChild(BoxTypes.DATA_REFERENCE_BOX);
		//TODO: support URNs
		if(dref.containsChild(BoxTypes.DATA_ENTRY_URL_BOX)) {
			DataEntryUrlBox url = (DataEntryUrlBox) dref.getChild(BoxTypes.DATA_ENTRY_URL_BOX);
			inFile = url.isInFile();
			try {
				location = new URL(url.getLocation());
			}
			catch(MalformedURLException e) {
				location = null;
			}
		}
		/*else if(dref.containsChild(BoxTypes.DATA_ENTRY_URN_BOX)) {
		DataEntryUrnBox urn = (DataEntryUrnBox) dref.getChild(BoxTypes.DATA_ENTRY_URN_BOX);
		inFile = urn.isInFile();
		location = urn.getLocation();
		}*/
		else {
			inFile = true;
			location = null;
		}

		//stbl
		final ContainerBox stbl = (ContainerBox) minf.getChild(BoxTypes.SAMPLE_TABLE_BOX);
		parseSampleTable(stbl);
	}

	private void parseSampleTable(ContainerBox stbl) {
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX);
		final SampleEntry[] entries = stsd.getSampleEntries();
		/*switch(type) {

		}*/
	}

	public abstract Type getType();

	//tkhd
	/**
	 * Returns true if the track is enabled. A disabled track is treated as if
	 * it were not present.
	 * @return true if the track is enabled
	 */
	public boolean isEnabled() {
		return tkhd.isTrackEnabled();
	}

	/**
	 * Returns true if the track is used in the presentation.
	 * @return true if the track is used
	 */
	public boolean isUsed() {
		return tkhd.isTrackInMovie();
	}

	/**
	 * Returns true if the track is used in previews.
	 * @return true if the track is used in previews
	 */
	public boolean isUsedForPreview() {
		return tkhd.isTrackInPreview();
	}

	/**
	 * Returns the time this track was created.
	 * @return the creation time
	 */
	public Date getCreationTime() {
		return Utils.getDate(tkhd.getCreationTime());
	}

	/**
	 * Returns the last time this track was modified.
	 * @return the modification time
	 */
	public Date getModificationTime() {
		return Utils.getDate(tkhd.getModificationTime());
	}

	//mdhd
	/**
	 * Returns the language for this media.
	 * @return the language
	 */
	public Locale getLanguage() {
		return new Locale(mdhd.getLanguage());
	}

	/**
	 * Returns true if the data for this track is present in this file (stream).
	 * If not, <code>getLocation()</code> returns the URL where the data can be
	 * found.
	 * @return true if the data is in this file (stream), false otherwise
	 */
	public boolean isInFile() {
		return inFile;
	}

	/**
	 * If the data for this track is not present in this file (if
	 * <code>isInFile</code> returns false), this method returns the data's
	 * location. Else null is returned.
	 * @return the data's location or null if the data is in this file
	 */
	public URL getLocation() {
		return location;
	}
}
