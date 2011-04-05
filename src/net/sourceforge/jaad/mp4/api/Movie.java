package net.sourceforge.jaad.mp4.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.mp4.boxes.impl.HandlerBox;
import net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox;

public class Movie {

	private final MovieHeaderBox mvhd;
	private final List<Track> tracks;
	private MetaData metaData;

	public Movie(ContainerBox cb) {
		mvhd = (MovieHeaderBox) cb.getChild(BoxTypes.MOVIE_HEADER_BOX);
		List<Box> trackBoxes = cb.getChildren(BoxTypes.TRACK_BOX);
		tracks = new ArrayList<Track>(trackBoxes.size());
		for(int i = 0; i<trackBoxes.size(); i++) {
			tracks.add(createTrack(trackBoxes.get(i)));
		}

		if(cb.containsChild(BoxTypes.META_BOX)) metaData = new MetaData((ContainerBox) cb.getChild(BoxTypes.META_BOX));
		else if(cb.containsChild(BoxTypes.USER_DATA_BOX)) {
			final ContainerBox udta = (ContainerBox) cb.getChild(BoxTypes.USER_DATA_BOX);
			if(udta.containsChild(BoxTypes.META_BOX)) metaData = new MetaData((ContainerBox) udta.getChild(BoxTypes.META_BOX));
		}
	}

	//TODO: support hint and meta
	private Track createTrack(Box trak) {
		final ContainerBox mdia = (ContainerBox) ((ContainerBox) trak).getChild(BoxTypes.MEDIA_BOX);
		final HandlerBox hdlr = (HandlerBox) mdia.getChild(BoxTypes.HANDLER_BOX);
		final Track track;
		switch((int) hdlr.getHandlerType()) {
			case HandlerBox.TYPE_VIDEO:
				track = new VideoTrack(trak);
				break;
			case HandlerBox.TYPE_SOUND:
				track = new AudioTrack(trak);
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
