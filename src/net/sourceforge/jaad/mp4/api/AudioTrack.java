package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.ContainerBox;
import net.sourceforge.jaad.mp4.boxes.impl.SoundMediaHeaderBox;

public class AudioTrack extends Track {

	private SoundMediaHeaderBox smhd;

	public AudioTrack(Box trak) {
		super(trak);

		final ContainerBox mdia = (ContainerBox) ((ContainerBox) trak).getChild(BoxTypes.MEDIA_BOX);
		final ContainerBox minf = (ContainerBox) mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX);
		smhd = (SoundMediaHeaderBox) minf.getChild(BoxTypes.SOUND_MEDIA_HEADER_BOX);
	}

	@Override
	public Type getType() {
		return Type.AUDIO;
	}

	/**
	 * The balance is a floating-point number that places mono audio tracks in a
	 * stereo space: 0 is centre (the normal value), full left is -1.0 and full
	 * right is 1.0.
	 *
	 * @return the stereo balance for a this track
	 */
	public double getBalance() {
		return smhd.getBalance();
	}
}
