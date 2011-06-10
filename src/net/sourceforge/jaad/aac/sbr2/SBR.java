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
	//processing buffers
	private final float[][][] W; //analysis QMF output
	private final float[][][] Xleft, Xright; //Xlow for both channels
	//filterbanks
	private final AnalysisFilterbank qmfA;
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

		W = new float[32][32][2];
		Xleft = new float[32][40][2];
		Xright = new float[32][40][2];

		qmfA = new AnalysisFilterbank();

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

		dequantSingle(0);
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

			dequantCoupled();
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

			dequantSingle(0);
			dequantSingle(1);
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

	/*======================= dequantization ====================== */
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
	private void dequantCoupled() {
		final int ampRes = header.getAmpRes() ? 1 : 0;
		//envelopes
		final double a = header.getAmpRes() ? 1 : 2;
		final double[][] e0 = cd[0].getEnvelopeScalefactors();
		final double[][] e1 = cd[1].getEnvelopeScalefactors();
		final int[] r = cd[0].getFrequencyResolutions();
		final int le = cd[0].getEnvCount();

		double d1, d2, d3;
		for(int l = 0; l<le; l++) {
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
		final int lq = cd[0].getNoiseCount();
		final int nq = tables.getNq();

		for(int l = 0; l<lq; l++) {
			for(int k = 0; k<nq; k++) {
				d1 = Math.pow(2, NOISE_FLOOR_OFFSET-q0[k][l]+1);
				d2 = Math.pow(2, PAN_OFFSETS[ampRes]-q1[k][l]);
				d3 = Math.pow(2, q1[k][l]-PAN_OFFSETS[ampRes]);
				q0[k][l] = d1/(1+d2);
				q0[k][l] = d1/(1+d3);
			}
		}
	}

	/*========================= processing =========================*/
	public boolean isPSUsed() {
		return psUsed;
	}

	//channel: 1024 time samples
	public void processSingleFrame(float[] channel, boolean downSampled) {
		//analysis (channel -> W -> Xlow)
		qmfA.process(channel, W, 0);
		copyLF(Xleft);

		//HF generator (Xlow -> Xhigh)
		//HF adjuster (Xhigh -> Y)
		//synthesis (Xlow/Xhigh/Y -> channel)
	}

	public void processSingleFramePS(float[] left, float[] right, boolean downSampled) {
		//analysis (channel -> W -> Xlow)
		qmfA.process(left, W, 0);
		copyLF(Xleft);
		qmfA.process(right, W, 0);
		copyLF(Xright);

		//HF generator (Xlow -> Xhigh)
		//HF adjuster (Xhigh -> Y)
		//synthesis (Xlow/Xhigh/Y -> channel)
	}

	public void processCoupleFrame(float[] left, float[] right, boolean downSampled) {
		//analysis (channel -> W -> Xlow)
		qmfA.process(left, W, 0);
		copyLF(Xleft);
		qmfA.process(right, W, 0);
		copyLF(Xright);

		//HF generator (Xlow -> Xhigh)
		//HF adjuster (Xhigh -> Y)
		//synthesis (Xlow/Xhigh/Y -> channel)
	}

	private void copyLF(float[][][] Xlow) {
		int k, l;
		//copies output from analysis QMF (W) to Xlow according to 4.6.18.5
		for(k = 0; k<tables.getKx(true); k++) {
			for(l = 0; l<T_HF_GEN; l++) {
				Xlow[k][l][0] = W[l+TIME_SLOTS_RATE-T_HF_GEN][k][0];
				Xlow[k][l][1] = W[l+TIME_SLOTS_RATE-T_HF_GEN][k][1];
			}
		}
		for(k = 0; k<tables.getKx(false); k++) {
			for(l = T_HF_GEN; l<TIME_SLOTS_RATE+T_HF_GEN; l++) {
				Xlow[k][l][0] = W[l-T_HF_GEN][k][0];
				Xlow[k][l][1] = W[l-T_HF_GEN][k][1];
			}
		}
	}
}
