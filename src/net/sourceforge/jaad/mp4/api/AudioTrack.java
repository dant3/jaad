package net.sourceforge.jaad.mp4.api;

import java.util.List;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.ESDBox;
import net.sourceforge.jaad.mp4.boxes.impl.EntryDescriptor;
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import net.sourceforge.jaad.mp4.boxes.impl.SoundMediaHeaderBox;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;

public class AudioTrack extends Track {

	private SoundMediaHeaderBox smhd;
	private AudioSampleEntry mp4a;
	private EntryDescriptor decoderSpecificInfo;

	public AudioTrack(Box trak, MP4InputStream in) {
		super(trak, in);

		final Box mdia = trak.getChild(BoxTypes.MEDIA_BOX);
		final Box minf = mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX);
		smhd = (SoundMediaHeaderBox) minf.getChild(BoxTypes.SOUND_MEDIA_HEADER_BOX);

		final Box stbl = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX);
		//sample descriptions
		final SampleDescriptionBox stsd = (SampleDescriptionBox) stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX);
		if(stsd!=null) {
			final SampleEntry[] sampleEntries = stsd.getSampleEntries();
			mp4a = (AudioSampleEntry) sampleEntries[0];
			final Box esds = mp4a.getChild(BoxTypes.ESD_BOX);
			if(esds!=null) findDecoderSpecificInfo((ESDBox) esds);
		}
	}

	//TODO: implement other entry descriptors
	private void findDecoderSpecificInfo(ESDBox esds) {
		final EntryDescriptor ed = esds.getEntryDescriptor();
		final List<EntryDescriptor> children = ed.getChildren();
		List<EntryDescriptor> children2;

		for(EntryDescriptor e : children) {
			children2 = e.getChildren();
			for(EntryDescriptor e2 : children2) {
				switch(e2.getType()) {
					case EntryDescriptor.TYPE_DECODER_SPECIFIC_INFO_DESCRIPTOR:
						decoderSpecificInfo = e2;
						break;
				}
			}
		}
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

	/**
	 * Returns the number of channels in this audio track.
	 * @return the number of channels
	 */
	public int getChannelCount() {
		return mp4a.getChannelCount();
	}

	/**
	 * Returns the sample rate of this audio track.
	 * @return the sample rate
	 */
	public int getSampleRate() {
		return mp4a.getSampleRate();
	}

	/**
	 * Returns the sample size in bits for this track.
	 * @return the sample size
	 */
	public int getSampleSize() {
		return mp4a.getSampleSize();
	}

	/**
	 * Returns the contents of the DecoderSpecificInfo entry descriptor as
	 * a byte array. It can be used to read the AAC DecoderConfig from.
	 *
	 * @return the decoder specific info
	 */
	public byte[] getDSID() {
		//TODO: is this only in audio track?
		return decoderSpecificInfo.getDSID();
	}
}
