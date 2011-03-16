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
package jaad.spi.jmf;

import jaad.util.mp4.AudioFrame;
import jaad.util.mp4.MP4Exception;
import jaad.util.mp4.MP4Reader;
import java.io.IOException;
import java.io.InputStream;
import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Demultiplexer;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.TrackListener;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

public class MP4Demultiplexer implements Demultiplexer {

	private static final String NAME = "MP4 Demultiplexer";
	private static final ContentDescriptor[] DESCRIPTORS = {new ContentDescriptor("MP4"), new ContentDescriptor("M4A")};
	private final InputStream in;
	private final AudioTrack track;
	private MP4Reader mp4;

	public MP4Demultiplexer(InputStream in) throws IOException {
		this.in = in;
		track = new AudioTrack();
	}

	/* ========== duration interface ========== */
	public Time getDuration() {
		return new Time(mp4.getDuration());
	}

	/* =========== plugin interface =========== */
	public String getName() {
		return NAME;
	}

	public void open() throws ResourceUnavailableException {
	}

	public void close() {
	}

	public void reset() {
	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String string) {
		return null;
	}

	/* ========== media handler interface ========== */
	public void setSource(DataSource ds) throws IOException, IncompatibleSourceException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/* ========== demultiplexer interface ========== */
	public Time getMediaTime() {
		return new Time(mp4.getCurrentTime());
	}

	public ContentDescriptor[] getSupportedInputContentDescriptors() {
		return DESCRIPTORS;
	}

	public Track[] getTracks() throws IOException, BadHeaderException {
		try {
			mp4 = new MP4Reader(in);
		}
		catch(MP4Exception e) {
			if(e.getType()==MP4Exception.MOVIE_BOX_AT_END) throw new BadHeaderException();
			else throw new IOException(e.toString());
		}
		return new Track[]{track};
	}

	public boolean isPositionable() {
		return false; //todo
	}

	public boolean isRandomAccess() {
		return false;
	}

	public Time setPosition(Time time, int i) {
		throw new UnsupportedOperationException("no supported"); //todo: implement in mp4
	}

	public void start() throws IOException {
	}

	public void stop() {
	}

	private class AudioTrack implements Track {

		private static final String FORMAT_NAME = "MP4A";
		private final AudioFormat format;
		private TrackListener listener;
		private AudioFrame frame;
		private byte[] data;
		private long sequenceNumber;

		AudioTrack() {
			format = new AudioFormat(FORMAT_NAME, mp4.getTimeScale(), Format.NOT_SPECIFIED, (int) mp4.getChannelCount());
			sequenceNumber = 0;
		}

		public Format getFormat() {
			return format;
		}

		public void setEnabled(boolean bln) {
		}

		public boolean isEnabled() {
			return true;
		}

		public Time getStartTime() {
			return new Time(0);
		}

		public void readFrame(Buffer buffer) {
			if(!mp4.hasMoreFrames()) {
				buffer.setFlags(Buffer.FLAG_EOM);
				return;
			}
			try {
				frame = mp4.readNextFrame();
				if(frame==null) {
					buffer.setFlags(Buffer.FLAG_EOM);
					return;
				}
				data = frame.getData();
				buffer.setData(data);
				buffer.setLength(data.length);
				buffer.setTimeStamp((long) frame.getTime());
				buffer.setFormat(format);
				buffer.setSequenceNumber(sequenceNumber);
				sequenceNumber++;
			}
			catch(IOException e) {
				buffer.setFlags(Buffer.FLAG_EOM); //todo: other flag??
			}
		}

		public int mapTimeToFrame(Time time) {
			int i = mp4.getFrameIndex(time.getSeconds());
			if(i==-1) i = FRAME_UNKNOWN;
			return i;
		}

		public Time mapFrameToTime(int i) {
			return new Time(mp4.getTimeStamp(i));
		}

		public void setTrackListener(TrackListener tl) {
			listener = tl;
		}

		public Time getDuration() {
			return new Time(mp4.getDuration());
		}
	}
}
