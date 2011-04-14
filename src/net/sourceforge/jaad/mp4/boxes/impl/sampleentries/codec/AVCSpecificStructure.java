package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;

//defined in ISO 14496-15 as 'AVC Configuration Record'
public class AVCSpecificStructure extends CodecSpecificStructure {

	private int configurationVersion, profile, level, lengthSize;
	private byte profileCompatibility;
	private int[] sequenceParameterSetLength, pictureParameterSetLength;
	private long[] sequenceParameterSetNALUnit, pictureParameterSetNALUnit;

	public AVCSpecificStructure() {
		super(0);
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		configurationVersion = in.read();
		profile = in.read();
		profileCompatibility = (byte) in.read();
		level = in.read();
		//6 bits reserved, 2 bits 'length size minus one'
		lengthSize = (in.read()&3)+1;
		//3 bits reserved, 5 bits number of sequence parameter sets
		final int sequenceParameterSets = in.read()&31;
		for(int i = 0; i<sequenceParameterSets; i++) {
			sequenceParameterSetLength[i] = (int) in.readBytes(2);
			sequenceParameterSetNALUnit[i] = in.readBytes(sequenceParameterSetLength[i]);
		}
		final int pictureParameterSets = in.read();
		for(int i = 0; i<pictureParameterSets; i++) {
			pictureParameterSetLength[i] = (int) in.readBytes(2);
			pictureParameterSetNALUnit[i] = in.readBytes(pictureParameterSetLength[i]);
		}
	}

	public int getConfigurationVersion() {
		return configurationVersion;
	}

	/**
	 * The AVC profile code as defined in ISO/IEC 14496-10.
	 *
	 * @return the AVC profile
	 */
	public int getProfile() {
		return profile;
	}

	/**
	 * The profileCompatibility is a byte defined exactly the same as the byte
	 * which occurs between the profileIDC and levelIDC in a sequence parameter
	 * set (SPS), as defined in ISO/IEC 14496-10.
	 *
	 * @return the profile compatibility byte
	 */
	public byte getProfileCompatibility() {
		return profileCompatibility;
	}

	public int getLevel() {
		return level;
	}

	/**
	 * The length in bytes of the NALUnitLength field in an AVC video sample or
	 * AVC parameter set sample of the associated stream. The value of this
	 * field 1, 2, or 4 bytes.
	 *
	 * @return the NALUnitLength length in bytes
	 */
	public int getLengthSize() {
		return lengthSize;
	}

	/**
	 * The sequence parameter set length indicates the length in bytes of the
	 * SPS NAL unit as defined in ISO/IEC 14496-10.
	 *
	 * @return the SPS lengths for all SPS NAL units.
	 */
	public int[] getSequenceParameterSetLengths() {
		return sequenceParameterSetLength;
	}

	/**
	 * The SPS NAL units, as specified in ISO/IEC 14496-10. SPSs shall occur in
	 * order of ascending parameter set identifier with gaps being allowed.
	 *
	 * @return all SPS NAL units
	 */
	public long[] getSequenceParameterSetNALUnits() {
		return sequenceParameterSetNALUnit;
	}

	/**
	 * The picture parameter set length indicates the length in bytes of the
	 * PPS NAL unit as defined in ISO/IEC 14496-10.
	 *
	 * @return the PPS lengths for all PPS NAL units.
	 */
	public int[] getPictureParameterSetLengths() {
		return pictureParameterSetLength;
	}

	/**
	 * The PPS NAL units, as specified in ISO/IEC 14496-10. PPSs shall occur in
	 * order of ascending parameter set identifier with gaps being allowed.
	 *
	 * @return all PPS NAL units
	 */
	public long[] getPictureParameterSetNALUnits() {
		return pictureParameterSetNALUnit;
	}
}
