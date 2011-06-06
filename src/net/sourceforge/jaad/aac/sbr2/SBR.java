package net.sourceforge.jaad.aac.sbr2;

import java.util.logging.Level;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.SampleFrequency;
import net.sourceforge.jaad.aac.ps.PS;
import net.sourceforge.jaad.aac.syntax.BitStream;
import net.sourceforge.jaad.aac.syntax.Constants;

public class SBR implements SBRConstants {

	//arguments
	private int sampleFrequency;
	private boolean downSampled;
	//objects
	private final SBRHeader header;
	private final ChannelData[] cd;
	private final FrequencyTables tables;
	//PS extension
	private PS ps;
	private boolean psUsed;

	public SBR(SampleFrequency sf, boolean downSampled) {
		sampleFrequency = sf.getFrequency()*2;
		this.downSampled = downSampled;

		header = new SBRHeader();
		cd = new ChannelData[2];
		cd[0] = new ChannelData();
		cd[1] = new ChannelData();
		tables = new FrequencyTables();

		psUsed = false;
	}

	/*========================= decoding =========================*/
	public void decode(BitStream in, int count, boolean stereo, boolean crc) throws AACException {
		final int pos = in.getPosition();

		if(crc) {
			Constants.LOGGER.info("SBR CRC bits present");
			in.skipBits(10); //TODO: implement crc check
		}

		//header flag
		if(in.readBool()) header.decode(in);

		//if at least one header was present yet: decode, else skip
		if(header.isDecoded()) {
			decodeData(in, stereo);

			//check for remaining bits (byte-align) and skip them
			final int len = in.getPosition()-pos;
			final int bitsLeft = count-len;
			if(bitsLeft>=8) Constants.LOGGER.log(Level.WARNING, "SBR: bits left: {0}", bitsLeft);
			else if(bitsLeft<0) throw new AACException("SBR data overread: "+bitsLeft);
			in.skipBits(bitsLeft);
		}
		else {
			final int left = count-pos+in.getPosition();
			in.skipBits(left);
			Constants.LOGGER.log(Level.INFO, "SBR frame without header, skipped {0} bits", left);
		}
	}

	private void decodeData(BitStream in, boolean stereo) throws AACException {
		if(stereo) decodeChannelPairElement(in);
		else decodeSingleChannelElement(in);

		//extended data
		if(in.readBool()) {
			int count = in.readBits(4);
			if(count==15) count += in.readBits(8);
			int bitsLeft = 8*count;

			int extensionID;
			while(bitsLeft>7) {
				bitsLeft -= 2;
				extensionID = in.readBits(2);
				bitsLeft -= decodeExtension(in, extensionID);
			}
			if(bitsLeft>0) in.skipBits(bitsLeft);
		}
	}

	private void decodeSingleChannelElement(BitStream in) throws AACException {
		if(in.readBool()) in.skipBits(4); //reserved

		cd[0].decodeGrid(in, header);
		cd[0].decodeDTDF(in);
		cd[0].decodeInvf(in, header);
		cd[0].decodeEnvelope(in, header, false, false);
		cd[0].decodeNoise(in, header, false, false);
		cd[0].decodeSinusoidal(in, header);
	}

	private void decodeChannelPairElement(BitStream in) throws AACException {
		if(in.readBool()) in.skipBits(8); //reserved

		final boolean ampRes = header.getAmpRes();
		if(in.readBool()) {
			cd[0].decodeGrid(in, header);
			cd[1].copyGrid(cd[0]);
			cd[0].decodeDTDF(in);
			cd[1].decodeDTDF(in);
			cd[0].decodeInvf(in, header);
			cd[1].copyInvf(cd[0]);
			cd[0].decodeEnvelope(in, header, false, true);
			cd[0].decodeNoise(in, header, false, true);
			cd[1].decodeEnvelope(in, header, true, true);
			cd[1].decodeNoise(in, header, true, true);
		}
		else {
			cd[0].decodeGrid(in, header);
			cd[1].decodeGrid(in, header);
			cd[0].decodeDTDF(in);
			cd[1].decodeDTDF(in);
			cd[0].decodeInvf(in, header);
			cd[1].decodeInvf(in, header);
			cd[0].decodeEnvelope(in, header, false, false);
			cd[1].decodeEnvelope(in, header, true, false);
			cd[0].decodeNoise(in, header, false, false);
			cd[1].decodeNoise(in, header, true, false);
		}

		cd[0].decodeSinusoidal(in, header);
		cd[1].decodeSinusoidal(in, header);
	}

	private int decodeExtension(BitStream in, int extensionID) throws AACException {
		int ret;

		switch(extensionID) {
			case EXTENSION_ID_PS:
				if(ps==null) ps = new PS();
				ret = ps.decode(in);
				if(!psUsed&&ps.hasHeader()) psUsed = true;
				else ret = 0;
				break;
			default:
				in.skipBits(6); //extension data
				ret = 6;
				break;
		}
		return ret;
	}

	/*========================= processing =========================*/
	public boolean isPSUsed() {
		return psUsed;
	}

	public void processSingleFrame(float[] channel, boolean downSampled) {
	}

	public void processSingleFramePS(float[] left, float[] right, boolean downSampled) {
	}

	public void processCoupleFrame(float[] left, float[] right, boolean downSampled) {
	}
}
