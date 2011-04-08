package net.sourceforge.jaad.mp4.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.HandlerBox;
import net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox;

public class Movie {

	private final MP4InputStream in;
	private final MovieHeaderBox mvhd;
	private final List<Track> tracks;
	private MetaData metaData;

	public Movie(Box box, MP4InputStream in) {
		this.in = in;

		mvhd = (MovieHeaderBox) box.getChild(BoxTypes.MOVIE_HEADER_BOX);
		List<Box> trackBoxes = box.getChildren(BoxTypes.TRACK_BOX);
		tracks = new ArrayList<Track>(trackBoxes.size());
		for(int i = 0; i<trackBoxes.size(); i++) {
			tracks.add(createTrack(trackBoxes.get(i)));
		}

		if(box.containsChild(BoxTypes.META_BOX)) metaData = new MetaData(box.getChild(BoxTypes.META_BOX));
		else if(box.containsChild(BoxTypes.USER_DATA_BOX)) {
			final Box udta = box.getChild(BoxTypes.USER_DATA_BOX);
			if(udta.containsChild(BoxTypes.META_BOX)) metaData = new MetaData(udta.getChild(BoxTypes.META_BOX));
		}
		else metaData = new MetaData();
	}

	//TODO: support hint and meta
	private Track createTrack(Box trak) {
		final HandlerBox hdlr = (HandlerBox) trak.getChild(BoxTypes.MEDIA_BOX).getChild(BoxTypes.HANDLER_BOX);
		final Track track;
		switch((int) hdlr.getHandlerType()) {
			case HandlerBox.TYPE_VIDEO:
				track = new VideoTrack(trak,in);
				break;
			case HandlerBox.TYPE_SOUND:
				track = new AudioTrack(trak,in);
				break;
			default:
				track = null;
		}
		return track;
	}

	/**
	 * Returns an unmodifiable list of all tracks in this movie. The tracks are
	 * ordered as they appeare in the file/stream.
	 *
	 * @return the tracks contained by this movie
	 */
	public List<Track> getTracks() {
		return Collections.unmodifiableList(tracks);
	}

	/**
	 * Returns an unmodifiable list of all tracks in this movie with the
	 * corresponding type. The tracks are ordered as they appeare in the
	 * file/stream.
	 *
	 * @return the tracks contained by this movie with the passed type
	 */
	public List<Track> getTracks(Track.Type type) {
		final List<Track> l = new ArrayList<Track>();
		for(Track t : tracks) {
			if(t.getType().equals(type)) l.add(t);
		}
		return Collections.unmodifiableList(l);
	}

	/**
	 * Returns the MetaData object for this movie.
	 *
	 * @return the MetaData for this movie
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	//mvhd
	/**
	 * Returns the time this movie was created.
	 * @return the creation time
	 */
	public Date getCreationTime() {
		return Utils.getDate(mvhd.getCreationTime());
	}

	/**
	 * Returns the last time this movie was modified.
	 * @return the modification time
	 */
	public Date getModificationTime() {
		return Utils.getDate(mvhd.getModificationTime());
	}

	/**
	 * Returns the duration in seconds.
	 * @return the duration
	 */
	public double getDuration() {
		return (double) mvhd.getDuration()/(double) mvhd.getTimeScale();
	}
}
