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
package jaad.util.mp4;

import jaad.util.mp4.boxes.*;
import jaad.util.mp4.boxes.SampleToChunkBox.SampleToChunkEntry;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An MP4 demultiplexer that can extract the DecoderSpecificInfo and all audio
 * frames from any MP4 stream.
 * @author in-somnia
 */
public class MP4Reader implements BoxTypes {

	private final MP4InputStream in;
	private final List<AudioFrame> frames;
	private int currentFrame;
	private List<Integer> samples;
	private List<SampleToChunkEntry> sampleToChunkEntries;
	private List<Long> chunks;
	private byte[] decoderSpecificInfo;
	private long sampleDuration;
	private double timeScale;
	private long duration, channels;

	/**
	 * Creates a demultiplexer that reads from the specified input stream.
	 * The boxes will be read an analyzed to find all necessary information.
	 * @param in input stream containing MP4 data
	 * @throws IOException if parsing fails
	 */
	public MP4Reader(InputStream in) throws IOException {
		this.in = new MP4InputStream(in);
		sampleDuration = 1024;
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
			type = box.getType();
			if(type==MOVIE_BOX) {
				moovFound = true;
				parseMoov((ContainerBox) box);
			}
			else if(type==MEDIA_DATA_BOX) {
				if(moovFound) break;
				else throw new MP4Exception("movie box at end of file", MP4Exception.MOVIE_BOX_AT_END);
			}
		}
	}

	private void parseMoov(ContainerBox moov) throws IOException {
		Box trak = null, minf = null, mdhd = null;
		ContainerBox mdia = null;

		final MovieHeaderBox mvhd = (MovieHeaderBox) moov.getChild(MOVIE_HEADER_BOX);
		if(mvhd!=null) {
			duration = mvhd.getDuration();
		}

		for(int i = 0; (trak = moov.getChild(TRACK_BOX, i))!=null; i++) {
			mdia = (ContainerBox) ((ContainerBox) trak).getChild(MEDIA_BOX);
			if(mdia!=null) {
				mdhd = mdia.getChild(MEDIA_HEADER_BOX);
				minf = mdia.getChild(MEDIA_INFORMATION_BOX);
				if(mdhd!=null&&minf!=null) {
					parseMedia((ContainerBox) minf, (MediaHeaderBox) mdhd);
				}
			}
		}
	}

	private void parseMedia(ContainerBox minf, MediaHeaderBox mdhd) throws IOException {
		final Box smhd = minf.getChild(SOUND_MEDIA_HEADER_BOX);
		if(smhd!=null) {
			final ContainerBox stbl = (ContainerBox) minf.getChild(SAMPLE_TABLE_BOX);
			if(stbl!=null) parseSampleTable(stbl);
			timeScale = (mdhd).getTimeScale()*1.0;
		}
	}

	private void parseSampleTable(ContainerBox stbl) throws IOException {
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(SAMPLE_DESCRIPTION_BOX);
		if(stsd!=null) {
			final AudioSampleEntryBox mp4a = stsd.getMP4A();
			channels = mp4a.getChannelCount();
			final Box esds = mp4a.getChild(ESD_BOX);
			if(esds!=null) findDecoderSpecificInfo((ESDBox) esds);
		}

		final TimeToSampleBox stts = (TimeToSampleBox) stbl.getChild(TIME_TO_SAMPLE_BOX);
		if(stts!=null) {
			sampleDuration = stts.getSampleDuration();
		}

		final SampleSizeBox stsz = (SampleSizeBox) stbl.getChild(SAMPLE_SIZE_BOX);
		if(stsz!=null) {
			samples = stsz.getSamples();
		}

		final SampleToChunkBox stsc = (SampleToChunkBox) stbl.getChild(SAMPLE_TO_CHUNK_BOX);
		if(stsz!=null) {
			sampleToChunkEntries = stsc.getEntries();
		}

		final ChunkOffsetBox stco = (ChunkOffsetBox) stbl.getChild(CHUNK_OFFSET_BOX);
		if(stsz!=null) {
			if(chunks==null) chunks = stco.getChunks();
			else chunks.addAll(stco.getChunks());
		}

		final ChunkOffsetBox co64 = (ChunkOffsetBox) stbl.getChild(CHUNK_LARGE_OFFSET_BOX);
		if(co64!=null) {
			if(chunks==null) chunks = co64.getChunks();
			else chunks.addAll(co64.getChunks());
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
						if(e2.getType()==EntryDescriptor.DECODER_SPECIFIC_INFO_DESCRIPTOR) {
							decoderSpecificInfo = e2.getDSID();
							return;
						}
					}
				}
			}
		}
	}

	private void analyseContent() {
		int sample = 1;
		long pos;

		//add the audio frames / samples / chunks
		SampleToChunkEntry record, nextRecord;
		int firstChunk, lastChunk, sampleCount, size;
		double ts;
		for(int i = 0; i<sampleToChunkEntries.size(); i++) {
			record = sampleToChunkEntries.get(i);
			firstChunk = record.getFirstChunk();
			lastChunk = chunks.size();
			if(i<sampleToChunkEntries.size()-1) {
				nextRecord = sampleToChunkEntries.get(i+1);
				lastChunk = nextRecord.getFirstChunk()-1;
			}
			for(int chunk = firstChunk; chunk<=lastChunk; chunk++) {
				sampleCount = record.getSamplesPerChunk();
				pos = chunks.get(chunk-1).longValue();
				while(sampleCount>0) {
					ts = (sampleDuration*(sample-1))/timeScale;
					size = samples.get(sample-1).intValue();
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

		chunks.clear();
		sampleToChunkEntries.clear();
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
	public double getTimeScale() {
		return timeScale;
	}

	/**
	 * Returns the audio track's duration.
	 * @return the duration
	 */
	public double getDuration() {
		return (double) duration/timeScale;
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
			final byte[] b = new byte[frame.getSize()];
			if(!in.readBytes(b)) throw new IOException("unexpected end of stream");
			frame.setData(b);
			currentFrame++;
		}
		return frame;
	}
}
