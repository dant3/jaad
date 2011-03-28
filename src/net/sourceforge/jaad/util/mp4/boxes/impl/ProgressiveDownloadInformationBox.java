package net.sourceforge.jaad.util.mp4.boxes.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import net.sourceforge.jaad.util.mp4.boxes.FullBox;

public class ProgressiveDownloadInformationBox extends FullBox {

	private Map<Long, Long> pairs;

	public ProgressiveDownloadInformationBox() {
		super("Progressive Download Information Box", "pdin");
		pairs = new HashMap<Long, Long>();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		long rate, initialDelay;
		while(left>0) {
			rate = in.readBytes(4);
			initialDelay = in.readBytes(4);
			pairs.put(rate, initialDelay);
		}
	}

	/**
	 * The map containing pairs of numbers specifying combinations of effective 
	 * file download bitrate in units of bytes/sec and a suggested initial 
	 * playback delay in units of milliseconds.
	 * A suitable initial delay can be obtained by linear interpolation between
	 * pairs, or by extrapolation from the first or last entry, using an
	 * estimated download rate.
	 * @return a map containing pairs of download bitrate and initial delay
	 */
	public Map<Long, Long> getInformationPairs() {
		return pairs;
	}
}
