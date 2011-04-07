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

import net.sourceforge.jaad.mp4.boxes.*;
import net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox;
import net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.TimeToSampleBox;
import net.sourceforge.jaad.mp4.boxes.impl.ESDBox;
import net.sourceforge.jaad.mp4.boxes.impl.EntryDescriptor;
import net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox.SampleToChunkEntry;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;

/**
 * An MP4 demultiplexer that can extract the DecoderSpecificInfo and all audio
 * frames from any MP4 stream.
 * @author in-somnia
 */
public class MP4Reader implements BoxTypes {

	private final MP4InputStream in;
	private final List<AudioFrame> frames;
	private int currentFrame;
	private long[] samples;
	private SampleToChunkEntry[] sampleToChunkEntries;
	private long[] chunks;
	private byte[] decoderSpecificInfo;
	private long[] sampleDuration;
	private double sampleRate;
	private long duration, channels;

	/**
	 * Creates a demultiplexer that reads from the specified input stream.
	 * The boxes will be read an analyzed to find all necessary information.
	 * @param in input stream containing MP4 data
	 * @throws IOException if parsing fails
	 */
	public MP4Reader(InputStream in) throws IOException {
		this.in = new MP4InputStream(in);
		sampleDuration = new long[]{1024};
		frames = new ArrayList<AudioFrame>();
		currentFrame = 0;

		readContent();
		analyseContent();
	}

	/* ========= analyzing ========== */
	private void readContent() throws IOException {
		Box box = null;
		long type;
		boolean moovFound = false;
		while(true) {
			box = BoxFactory.parseBox(null, in);
			//DEBUG:
			//System.out.println(box.toTreeString(0));
			type = box.getType();
			if(type==MOVIE_BOX) {
				moovFound = true;
				parseMoov(box);
			}
			else if(type==MEDIA_DATA_BOX) {
				if(moovFound) break;
				else throw new MP4Exception("movie box at end of file");
			}
		}
	}

	private void parseMoov(Box moov) throws IOException {
		Box minf = null, mdhd = null, mdia = null;

		final MovieHeaderBox mvhd = (MovieHeaderBox) moov.getChild(MOVIE_HEADER_BOX);
		if(mvhd!=null) duration = mvhd.getDuration();

		final List<Box> tracks = moov.getChildren(TRACK_BOX);

		for(Box trak : tracks) {
			mdia = trak.getChild(MEDIA_BOX);
			if(mdia!=null) {
				mdhd = mdia.getChild(MEDIA_HEADER_BOX);
				minf = mdia.getChild(MEDIA_INFORMATION_BOX);
				if(mdhd!=null&&minf!=null) {
					parseMedia(minf, (MediaHeaderBox) mdhd);
				}
			}
		}
	}

	private void parseMedia(Box minf, MediaHeaderBox mdhd) throws IOException {
		final Box smhd = minf.getChild(SOUND_MEDIA_HEADER_BOX);
		if(smhd!=null) {
			final Box stbl = minf.getChild(SAMPLE_TABLE_BOX);
			if(stbl!=null) parseSampleTable(stbl);
			sampleRate = mdhd.getTimeScale();
		}
	}

	private void parseSampleTable(Box stbl) throws IOException {
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(SAMPLE_DESCRIPTION_BOX);
		if(stsd!=null) {
			final SampleEntry[] sampleEntries = stsd.getSampleEntries();
			final AudioSampleEntry mp4a = (AudioSampleEntry) sampleEntries[0];
			channels = mp4a.getChannelCount();
			final Box esds = mp4a.getChild(ESD_BOX);
			if(esds!=null) findDecoderSpecificInfo((ESDBox) esds);
		}

		final TimeToSampleBox stts = (TimeToSampleBox) stbl.getChild(TIME_TO_SAMPLE_BOX);
		if(stts!=null) sampleDuration = stts.getSampleDeltas();

		final SampleSizeBox stsz = (SampleSizeBox) stbl.getChild(SAMPLE_SIZE_BOX);
		if(stsz!=null) samples = stsz.getSampleSizes();

		final SampleToChunkBox stsc = (SampleToChunkBox) stbl.getChild(SAMPLE_TO_CHUNK_BOX);
		if(stsc!=null) sampleToChunkEntries = stsc.getEntries();

		final ChunkOffsetBox stco = (ChunkOffsetBox) stbl.getChild(CHUNK_OFFSET_BOX);
		if(stco!=null) {
			if(chunks==null) chunks = stco.getChunks();
			else {
				final long[] stcoC = stco.getChunks();
				long[] tmp = new long[chunks.length+stcoC.length];
				System.arraycopy(chunks, 0, tmp, 0, chunks.length);
				System.arraycopy(stcoC, 0, tmp, chunks.length, stcoC.length);
				chunks = tmp;
			}
		}

		final ChunkOffsetBox co64 = (ChunkOffsetBox) stbl.getChild(CHUNK_LARGE_OFFSET_BOX);
		if(co64!=null) {
			if(chunks==null) chunks = co64.getChunks();
			else {
				final long[] co64C = stco.getChunks();
				long[] tmp = new long[chunks.length+co64C.length];
				System.arraycopy(chunks, 0, tmp, 0, chunks.length);
				System.arraycopy(co64C, 0, tmp, chunks.length, co64C.length);
				chunks = tmp;
			}
		}
	}

	private void findDecoderSpecificInfo(ESDBox esds) {
		final EntryDescriptor descriptor = esds.getEntryDescriptor();
		if(descriptor!=null) {
			final List<EntryDescriptor> children = descriptor.getChildren();
			List<EntryDescriptor> children2;
			for(EntryDescriptor e : children) {
				if(e.getChildren().size()>0) {
					children2 = e.getChildren();
					for(EntryDescriptor e2 : children2) {
						switch(e2.getType()) {
							case EntryDescriptor.TYPE_DECODER_SPECIFIC_INFO_DESCRIPTOR:
								decoderSpecificInfo = e2.getDSID();
								break;
						}
					}
				}
			}
		}
	}

	private void analyseContent() {
		int sample = 1;
		long pos;

		//add the audio frames
		SampleToChunkEntry record, nextRecord;
		long firstChunk, lastChunk, sampleCount, size;
		double ts;
		for(int i = 0; i<sampleToChunkEntries.length; i++) {
			record = sampleToChunkEntries[i];
			firstChunk = record.getFirstChunk();
			lastChunk = chunks.length;
			if(i<sampleToChunkEntries.length-1) {
				nextRecord = sampleToChunkEntries[i+1];
				lastChunk = nextRecord.getFirstChunk()-1;
			}
			for(long chunk = firstChunk; chunk<=lastChunk; chunk++) {
				sampleCount = record.getSamplesPerChunk();
				pos = chunks[(int) chunk-1];
				while(sampleCount>0) {
					ts = (sampleDuration[0]*(sample-1))/sampleRate;
					size = samples[sample-1];
					/* TODO: instantiating all frames is not necessary;
					 * perhaps save the values and don't instantiate until
					 * requested by readNextFrame()
					 * -> problem: how to sort?
					 */
					frames.add(new AudioFrame(pos, size, ts));

					pos += size;
					sampleCount--;
					sample++;
				}
			}
		}

		Collections.sort(frames);

		chunks = null;
		sampleToChunkEntries = null;
	}

	/* ========= properties ========== */
	/**
	 * Returns the DecoderSpecificInfo, that contains meta data about the audio
	 * data and can be used to construct a <code>DecoderConfig</code>.
	 * @return the decoder specific info
	 */
	public byte[] getDecoderSpecificInfo() {
		return decoderSpecificInfo;
	}

	/**
	 * Returns the current time offset.
	 * @return the current media time
	 */
	public double getCurrentTime() {
		return frames.get(currentFrame).getTime();
	}

	/**
	 * Returns the timestamp for a specific frame.
	 * @param frameIndex the frame index
	 * @return the timestamp for that frame
	 */
	public double getTimeStamp(int frameIndex) {
		return frames.get(frameIndex).getTime();
	}

	/**
	 * Searches through the frames to find the specified timestamp.
	 * @param timeStamp the timestamp to look for
	 * @return the frame index
	 */
	public int getFrameIndex(double timeStamp) {
		int index = -1;
		AudioFrame frame;
		for(int i = 0; i<frames.size(); i++) {
			frame = frames.get(i);
			if(frame.getTime()==timeStamp) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Returns the audio time scale (sample rate).
	 * @return the time scale
	 */
	public double getSampleRate() {
		return sampleRate;
	}

	/**
	 * Returns the audio track's duration.
	 * @return the duration
	 */
	public double getDuration() {
		return (double) duration/sampleRate;
	}

	/**
	 * Returns the number of audio channels.
	 * @return the audio channel count
	 */
	public long getChannelCount() {
		return channels;
	}

	/* ========= reading ========= */
	/**
	 * Indicates if there are more audio frames to be read.
	 * @return true if there is at least one more frame
	 */
	public boolean hasMoreFrames() {
		return currentFrame<frames.size();
	}

	/**
	 * Reads the next audio frame from the input stream.
	 * @return the read audio frame
	 * @throws IOException if reading fails
	 */
	public AudioFrame readNextFrame() throws IOException {
		AudioFrame frame = null;
		if(hasMoreFrames()) {
			frame = frames.get(currentFrame);
			final long diff = frame.getOffset()-in.getOffset();
			if(diff>0) in.skipBytes(diff);
			else if(diff<0) throw new IOException("invalid data: frame already skipped");
			final byte[] b = new byte[(int) frame.getSize()];
			if(!in.readBytes(b)) throw new IOException("unexpected end of stream");
			frame.setData(b);
			currentFrame++;
		}
		return frame;
	}
}
