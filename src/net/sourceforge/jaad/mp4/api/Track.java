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

import java.util.logging.Logger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox;
import net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrlBox;
import net.sourceforge.jaad.mp4.boxes.impl.DataReferenceBox;
import net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox;
import net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox.SampleToChunkEntry;
import net.sourceforge.jaad.mp4.boxes.impl.DecodingTimeToSampleBox;
import net.sourceforge.jaad.mp4.boxes.impl.TrackHeaderBox;
import net.sourceforge.jaad.mp4.od.DecoderSpecificInfo;
import net.sourceforge.jaad.mp4.boxes.impl.ESDBox;
import net.sourceforge.jaad.mp4.od.Descriptor;

/**
 * This class represents a track in a movie.
 *
 * Each track contains either a decoder specific info as a byte array or a
 * <code>DecoderInfo</code> object that contains necessary information for the
 * decoder.
 *
 * @author in-somnia
 */
//TODO: expand javadoc
public abstract class Track {

	public interface Codec {
		//TODO: currently only marker interface
	}
	private final MP4InputStream in;
	protected final TrackHeaderBox tkhd;
	private final MediaHeaderBox mdhd;
	private final boolean inFile;
	private final List<Frame> frames;
	private URL location;
	private int currentFrame;
	//info structures
	protected DecoderSpecificInfo decoderSpecificInfo;
	protected DecoderInfo decoderInfo;

	Track(Box trak, MP4InputStream in) {
		this.in = in;

		tkhd = (TrackHeaderBox) trak.getChild(BoxTypes.TRACK_HEADER_BOX);

		final Box mdia = trak.getChild(BoxTypes.MEDIA_BOX);
		mdhd = (MediaHeaderBox) mdia.getChild(BoxTypes.MEDIA_HEADER_BOX);
		final Box minf = mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX);

		final Box dinf = minf.getChild(BoxTypes.DATA_INFORMATION_BOX);
		final DataReferenceBox dref = (DataReferenceBox) dinf.getChild(BoxTypes.DATA_REFERENCE_BOX);
		//TODO: support URNs
		if(dref.hasChild(BoxTypes.DATA_ENTRY_URL_BOX)) {
			DataEntryUrlBox url = (DataEntryUrlBox) dref.getChild(BoxTypes.DATA_ENTRY_URL_BOX);
			inFile = url.isInFile();
			try {
				location = new URL(url.getLocation());
			}
			catch(MalformedURLException e) {
				Logger.getLogger("MP4 API").log(Level.WARNING, "Parsing URL-Box failed: {0}, url: {1}", new String[]{e.toString(), url.toString()});
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

		//sample table
		final Box stbl = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX);
		if(stbl.hasChildren()) {
			frames = new ArrayList<Frame>();
			parseSampleTable(stbl);
		}
		else frames = Collections.emptyList();
		currentFrame = 0;
	}

	private void parseSampleTable(Box stbl) {
		final double timeScale = mdhd.getTimeScale();

		//get tables from boxes
		final SampleToChunkEntry[] sampleToChunks = ((SampleToChunkBox) stbl.getChild(BoxTypes.SAMPLE_TO_CHUNK_BOX)).getEntries();
		final long[] sampleSizes = ((SampleSizeBox) stbl.getChild(BoxTypes.SAMPLE_SIZE_BOX)).getSampleSizes();
		final ChunkOffsetBox stco;
		if(stbl.hasChild(BoxTypes.CHUNK_OFFSET_BOX)) stco = (ChunkOffsetBox) stbl.getChild(BoxTypes.CHUNK_OFFSET_BOX);
		else stco = (ChunkOffsetBox) stbl.getChild(BoxTypes.CHUNK_LARGE_OFFSET_BOX);
		final long[] chunkOffsets = stco.getChunks();

		final DecodingTimeToSampleBox stts = (DecodingTimeToSampleBox) stbl.getChild(BoxTypes.DECODING_TIME_TO_SAMPLE_BOX);
		final long[] sampleCounts = stts.getSampleCounts();
		final long[] sampleDeltas = stts.getSampleDeltas();

		//decode sampleDurations
		final long[] sampleDurations = new long[sampleSizes.length];
		int off = 0;
		for(int i = 0; i<sampleCounts.length; i++) {
			for(int j = 0; j<sampleCounts[i]; j++) {
				sampleDurations[off+j] = sampleDeltas[i];
			}
			off += sampleCounts[i];
		}

		//create frames
		SampleToChunkEntry entry;
		int firstChunk, lastChunk;
		long pos, size;
		int j, s;
		double timeStamp;
		int current = 0;

		for(int i = 0; i<sampleToChunks.length; i++) {
			//an entry (run) contains several chunks with the same 'samples-per-chunk' value
			entry = sampleToChunks[i];
			firstChunk = (int) entry.getFirstChunk();
			//since the last chunk of a run is not specified: get it from the next run
			if(i<sampleToChunks.length-1) lastChunk = (int) sampleToChunks[i+1].getFirstChunk()-1;
			else lastChunk = chunkOffsets.length;

			//iterate over all chunks in this run
			for(j = firstChunk; j<=lastChunk; j++) {
				pos = chunkOffsets[j-1];
				//iterate over all samples in this chunk
				for(s = 0; s<entry.getSamplesPerChunk(); s++) {
					//create frame for sample
					timeStamp = (sampleDurations[j-1]*current)/timeScale;
					size = sampleSizes[current];
					frames.add(new Frame(getType(), pos, size, timeStamp));

					//calculate sampe offset from chunk offset and sample sizes
					pos += size;
					current++;
				}
			}
		}

		//frames need not to be time-ordered: sort by timestamp
		//TODO: is it possible to add them to the specific position?
		Collections.sort(frames);
	}

	//TODO: implement other entry descriptors
	protected void findDecoderSpecificInfo(ESDBox esds) {
		final Descriptor ed = esds.getEntryDescriptor();
		final List<Descriptor> children = ed.getChildren();
		List<Descriptor> children2;

		for(Descriptor e : children) {
			children2 = e.getChildren();
			for(Descriptor e2 : children2) {
				switch(e2.getType()) {
					case Descriptor.TYPE_DECODER_SPECIFIC_INFO:
						decoderSpecificInfo = (DecoderSpecificInfo) e2;
						break;
				}
			}
		}
	}

	public abstract Type getType();

	public abstract Codec getCodec();

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

	//info structures
	/**
	 * Returns the decoder specific info, if present. It contains configuration
	 * data for the decoder. If the decoder specific info is not present, the
	 * track contains a <code>DecoderInfo</code>.
	 *
	 * @see #getDecoderInfo() 
	 * @return the decoder specific info
	 */
	public byte[] getDecoderSpecificInfo() {
		return decoderSpecificInfo.getData();
	}

	/**
	 * Returns the <code>DecoderInfo</code>, if present. It contains 
	 * configuration information for the decoder. If the structure is not
	 * present, the track contains a decoder specific info.
	 *
	 * @see #getDecoderSpecificInfo()
	 * @return the codec specific structure
	 */
	public DecoderInfo getDecoderInfo() {
		return decoderInfo;
	}

	//reading
	/**
	 * Indicates if there are more frames to be read in this track.
	 * 
	 * @return true if there is at least one more frame to read.
	 */
	public boolean hasMoreFrames() {
		return currentFrame<frames.size();
	}

	/**
	 * Reads the next frame from this track. If it contains no more frames to
	 * read, null is returned.
	 * 
	 * @return the next frame or null if there are no more frames to read
	 * @throws IOException if reading fails
	 */
	public Frame readNextFrame() throws IOException {
		Frame frame = null;
		if(hasMoreFrames()) {
			frame = frames.get(currentFrame);

			final long diff = frame.getOffset()-in.getOffset();
			if(diff>0) in.skipBytes(diff);
			else if(diff<0) {
				if(in.hasRandomAccess()) in.seek(frame.getOffset());
				else {
					Logger.getLogger("MP4 API").log(Level.WARNING, "Track.readNextFrame failed: frame {0} already skipped, offset:{1}, stream:{2}", new Object[]{currentFrame, frame.getOffset(), in.getOffset()});
					throw new IOException("frame already skipped and no random access");
				}
			}

			final byte[] b = new byte[(int) frame.getSize()];
			if(!in.readBytes(b)) {
				Logger.getLogger("MP4 API").log(Level.WARNING, "Track.readNextFrame failed: tried to read {0} bytes at {1}", new Long[]{frame.getSize(), in.getOffset()});
				throw new IOException("unexpected end of stream");
			}
			frame.setData(b);
			currentFrame++;
		}
		return frame;
	}

	/**
	 * This method tries to seek to the frame that is nearest to the given
	 * timestamp. It returns the timestamp of the frame it seeked to or -1 if
	 * none was found.
	 * 
	 * @param timestamp a timestamp to seek to
	 * @return the frame's timestamp that the method seeked to
	 */
	public double seek(double timestamp) {
		//find first frame > timestamp
		Frame frame = null;
		for(int i = 0; i<frames.size(); i++) {
			frame = frames.get(i++);
			if(frame.getTime()>timestamp) {
				currentFrame = i;
				break;
			}
		}
		return (frame==null) ? -1 : frame.getTime();
	}

	/**
	 * Returns the timestamp of the next frame to be read. This is needed to
	 * read frames from a movie that contains multiple tracks.
	 *
	 * @return the next frame's timestamp
	 */
	double getNextTimeStamp() {
		return frames.get(currentFrame).getTime();
	}
}
