package net.sourceforge.jaad.util.mp4.boxes.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.jaad.util.mp4.MP4InputStream;
import net.sourceforge.jaad.util.mp4.boxes.FullBox;

public class ProgressiveDownloadInformationBox extends FullBox {

	private List<Long> rate, initialDelay;

	public ProgressiveDownloadInformationBox() {
		rate = new ArrayList<Long>();
		initialDelay = new ArrayList<Long>();
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);
		while(left>0) {
			rate.add(in.readBytes(4));
			initialDelay.add(in.readBytes(4));
		}
	}

	public List<Long> getInitialDelay() {
		return initialDelay;
	}

	public List<Long> getRate() {
		return rate;
	}
}
