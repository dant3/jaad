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
	//data
	private final SBRHeader header;
	private final ChannelData[] cd;
	private final FrequencyTables tables;
	private boolean coupling;
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
		tables.calculate(header, sampleFrequency); //TODO: only needed when header changes?

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
		cd[0].decodeInvf(in, header, tables);
		cd[0].decodeEnvelope(in, header, tables, false, false);
		cd[0].decodeNoise(in, header, tables, false, false);
		cd[0].decodeSinusoidal(in, header, tables);
	}

	private void decodeChannelPairElement(BitStream in) throws AACException {
		if(in.readBool()) in.skipBits(8); //reserved

		if(coupling = in.readBool()) {
			cd[0].decodeGrid(in, header);
			cd[1].copyGrid(cd[0]);
			cd[0].decodeDTDF(in);
			cd[1].decodeDTDF(in);
			cd[0].decodeInvf(in, header, tables);
			cd[1].copyInvf(cd[0]);
			cd[0].decodeEnvelope(in, header, tables, false, coupling);
			cd[0].decodeNoise(in, header, tables, false, coupling);
			cd[1].decodeEnvelope(in, header, tables, true, coupling);
			cd[1].decodeNoise(in, header, tables, true, coupling);
		}
		else {
			cd[0].decodeGrid(in, header);
			cd[1].decodeGrid(in, header);
			cd[0].decodeDTDF(in);
			cd[1].decodeDTDF(in);
			cd[0].decodeInvf(in, header, tables);
			cd[1].decodeInvf(in, header, tables);
			cd[0].decodeEnvelope(in, header, tables, false, coupling);
			cd[1].decodeEnvelope(in, header, tables, true, coupling);
			cd[0].decodeNoise(in, header, tables, false, coupling);
			cd[1].decodeNoise(in, header, tables, true, coupling);
		}

		cd[0].decodeSinusoidal(in, header, tables);
		cd[1].decodeSinusoidal(in, header, tables);
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
		dequant(false);
	}

	public void processSingleFramePS(float[] left, float[] right, boolean downSampled) {
		dequant(true);
	}

	public void processCoupleFrame(float[] left, float[] right, boolean downSampled) {
		dequant(true);
	}

	private void dequant(boolean pair) {
		if(pair) {
			if(coupling) dequantPair();
			else {
				dequantSingle(0);
				dequantSingle(1);
			}
		}
		else dequantSingle(0);
	}

	private void dequantSingle(int ch) {
		//envelopes
		final double a = header.getAmpRes() ? 1.0 : 2.0;
		final double[][] e = cd[ch].getEnvelopeScalefactors();
		final int[] freqRes = cd[ch].getFrequencyResolutions();

		for(int l = 0; l<cd[ch].getEnvCount(); l++) {
			for(int k = 0; k<tables.getN(freqRes[l]); k++) {
				e[k][l] = 64*Math.pow(2, (e[k][l]/a));
			}
		}

		//noise
		final double[][] q = cd[ch].getNoiseFloorData();
		for(int l = 0; l<cd[ch].getNoiseCount(); l++) {
			for(int k = 0; k<tables.getNq(); k++) {
				q[k][l] = Math.pow(2, NOISE_FLOOR_OFFSET-q[k][l]);
			}
		}
	}

	//dequantization of coupled channel pair
	private void dequantPair() {
		final int ampRes = header.getAmpRes() ? 1 : 0;
		//envelopes
		final double a = header.getAmpRes() ? 1 : 2;
		final double[][] e0 = cd[0].getEnvelopeScalefactors();
		final double[][] e1 = cd[1].getEnvelopeScalefactors();
		final int le = cd[0].getEnvCount();
		int[] r = cd[0].getFrequencyResolutions();

		double d1, d2, d3;
		for(int l = 0; l<cd[0].getEnvCount(); l++) {
			for(int k = 0; k<tables.getN(r[l]); k++) {
				d1 = Math.pow(2, (e0[k][l]/a)+1);
				d2 = Math.pow(2, (PAN_OFFSETS[ampRes]-e1[k][l])/a);
				d3 = Math.pow(2, (e1[k][l]-PAN_OFFSETS[ampRes])/a);
				e0[k][l] = 64*(d1/(1+d2));
				e1[k][l] = 64*(d1/(1+d3));
			}
		}

		//noise
		final double[][] q0 = cd[0].getNoiseFloorData();
		final double[][] q1 = cd[1].getNoiseFloorData();
		for(int l = 0; l<cd[0].getNoiseCount(); l++) {
			for(int k = 0; k<tables.getNq(); k++) {
				d1 = Math.pow(2, NOISE_FLOOR_OFFSET-q0[k][l]+1);
				d2 = Math.pow(2, PAN_OFFSETS[ampRes]-q1[k][l]);
				d3 = Math.pow(2, q1[k][l]-PAN_OFFSETS[ampRes]);
				q0[k][l] = d1/(1+d2);
				q0[k][l] = d1/(1+d3);
			}
		}
	}
}
