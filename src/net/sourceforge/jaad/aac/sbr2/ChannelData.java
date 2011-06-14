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

import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;

class ChannelData implements SBRConstants, HuffmanTables {

	//grid
	private boolean ampRes;
	private int frameClass;
	private int envCount, envCountPrev, noiseCount;
	private final int[] freqRes;
	private int freqResPrevious;
	private int varBord0, varBord1;
	private int relCount0, relCount1;
	private final int[] relativeBorders0, relativeBorders1;
	private int pointer;
	private int la, laPrevious;
	//dtdf
	private final boolean[] dfEnv, dfNoise;
	//invf
	private final int[] invfMode, invfModePrevious;
	//envelopes
	private final float[][] envelopeSF;
	private final float[] envelopeSFPrevious;
	private final int[] te; //envelope time borders, p.214
	//noise
	private final float[][] noiseFloorData;
	private final float[] noiseFDPrevious; //last of previous frame
	private final int[] tq; //noise floor time borders, p.215
	//sinusoidal
	private boolean sinusoidalsPresent;
	private final boolean[] sinusoidals;
	private boolean[] sIndexMappedPrevious;
	//chirp factors (calculated by HFGenerator)
	private final float[] bwArray;
	//indizes for assembling in HFAdjustment
	private int noiseIndex, sineIndex;

	ChannelData() {
		freqRes = new int[MAX_ENV_COUNT];
		invfMode = new int[MAX_NQ];
		invfModePrevious = new int[MAX_NQ];

		dfEnv = new boolean[MAX_ENV_COUNT];
		dfNoise = new boolean[MAX_NOISE_COUNT];

		envelopeSF = new float[MAX_ENV_COUNT][MAX_BANDS];
		envelopeSFPrevious = new float[MAX_BANDS];
		te = new int[MAX_ENV_COUNT+1];

		noiseFloorData = new float[MAX_NOISE_COUNT][MAX_BANDS];
		noiseFDPrevious = new float[MAX_BANDS];
		tq = new int[MAX_NOISE_COUNT+1];

		relativeBorders0 = new int[MAX_RELATIVE_BORDERS];
		relativeBorders1 = new int[MAX_RELATIVE_BORDERS];

		sinusoidals = new boolean[MAX_BANDS];

		bwArray = new float[MAX_CHIRP_FACTORS];
	}

	void savePreviousData() {
		//grid
		envCountPrev = envCount;
		freqResPrevious = freqRes[freqRes.length-1];
		//TODO: need to save dtdf?
		//invf
		System.arraycopy(invfMode, 0, invfModePrevious, 0, MAX_NQ);
		//envelopes
		System.arraycopy(envelopeSF[envCount-1], 0, envelopeSFPrevious, 0, MAX_BANDS);
		//noise
		System.arraycopy(noiseFloorData[noiseCount-1], 0, noiseFDPrevious, 0, MAX_BANDS);
	}

	/* ======================= decoding ======================*/
	void decodeGrid(BitStream in, SBRHeader header) throws AACException {
		final int bits;

		ampRes = header.getAmpRes();

		switch(frameClass = in.readBits(2)) {
			case FIXFIX:
				envCount = 1<<in.readBits(2);
				if(envCount==1) ampRes = false;
				//check requirement (4.6.18.6.3):
				else if(envCount>4) throw new AACException("SBR: too many envelopes: "+envCount);

				Arrays.fill(freqRes, 0, envCount, in.readBit());
				break;
			case FIXVAR:
				varBord1 = in.readBits(2);
				relCount1 = in.readBits(2);
				envCount = relCount1+1;

				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/LOG2); //TODO: replace with table
				pointer = in.readBits(bits);

				//TODO: fill remaining with zeros?
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
				}
				break;
			case VARFIX:
				varBord0 = in.readBits(2);
				relCount0 = in.readBits(2);
				envCount = relCount0+1;

				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/LOG2); //TODO: replace with table
				pointer = in.readBits(bits);

				for(int i = 0; i<envCount; i++) {
					freqRes[i] = in.readBit();
				}
				break;
			case VARVAR:
				varBord0 = in.readBits(2);
				varBord1 = in.readBits(2);
				relCount0 = in.readBits(2);
				relCount1 = in.readBits(2);
				envCount = relCount0+relCount1+1;
				//check requirement (4.6.18.6.3):
				if(envCount>5) throw new AACException("SBR: too many envelopes: "+envCount);

				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/LOG2); //TODO: replace with table
				pointer = in.readBits(bits);

				for(int i = 0; i<envCount; i++) {
					freqRes[i] = in.readBit();
				}
				break;
		}
		noiseCount = (envCount>1) ? 2 : 1;

		//calculate La (table 4.157)
		laPrevious = la;
		if((frameClass==FIXVAR||frameClass==VARVAR)&&pointer>0) la = envCount+1-pointer;
		else if(frameClass==VARFIX&&pointer>1) la = pointer-1;
		else la = -1;
	}

	void decodeDTDF(BitStream in) throws AACException {
		for(int i = 0; i<envCount; i++) {
			dfEnv[i] = in.readBool();
		}

		for(int i = 0; i<noiseCount; i++) {
			dfNoise[i] = in.readBool();
		}
	}

	void decodeInvf(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		for(int i = 0; i<tables.getNq(); i++) {
			invfMode[i] = in.readBits(2);
		}
	}

	void decodeEnvelope(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
		//select huffman codebooks
		final int[][] tHuff, fHuff;
		final int tLav, fLav;
		if(coupling&&secCh) {
			if(ampRes) {
				tHuff = T_HUFFMAN_ENV_BAL_3_0;
				tLav = T_HUFFMAN_ENV_BAL_3_0_LAV;
				fHuff = F_HUFFMAN_ENV_BAL_3_0;
				fLav = F_HUFFMAN_ENV_BAL_3_0_LAV;
			}
			else {
				tHuff = T_HUFFMAN_ENV_BAL_1_5;
				tLav = T_HUFFMAN_ENV_BAL_1_5_LAV;
				fHuff = F_HUFFMAN_ENV_BAL_1_5;
				fLav = F_HUFFMAN_ENV_BAL_1_5_LAV;
			}
		}
		else {
			if(ampRes) {
				tHuff = T_HUFFMAN_ENV_3_0;
				tLav = T_HUFFMAN_ENV_3_0_LAV;
				fHuff = F_HUFFMAN_ENV_3_0;
				fLav = F_HUFFMAN_ENV_3_0_LAV;
			}
			else {
				tHuff = T_HUFFMAN_ENV_1_5;
				tLav = T_HUFFMAN_ENV_1_5_LAV;
				fHuff = F_HUFFMAN_ENV_1_5;
				fLav = F_HUFFMAN_ENV_1_5_LAV;
			}
		}

		//read delta coded huffman data
		final int[] envBands = tables.getN();
		final int bits = 7-((secCh&&coupling) ? 1 : 0)-(ampRes ? 1 : 0);
		final int delta = ((secCh&&coupling) ? 1 : 0)+1;
		final int odd = envBands[1]&1;

		int j, k, frPrev;
		float[] prev;
		for(int i = 0; i<envCount; i++) {
			prev = (i==0) ? envelopeSFPrevious : envelopeSF[i-1];
			frPrev = (i==0) ? freqResPrevious : freqRes[i-1];

			if(dfEnv[i]) {
				if(freqRes[i]==frPrev) {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						envelopeSF[i][j] = prev[j]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
				else if(freqRes[i]==1) {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						k = (j+odd)>>1; //fLow[k] <= fHigh[j] < fLow[k + 1]
						envelopeSF[i][j] = prev[k]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
				else {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						k = (j!=0) ? (2*j-odd) : 0; //fHigh[k] == fLow[j]
						envelopeSF[i][j] = prev[k]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
			}
			else {
				envelopeSF[i][0] = delta*in.readBits(bits);
				for(j = 1; j<envBands[freqRes[i]]; j++) {
					envelopeSF[i][j] = envelopeSF[i][j-1]+delta*(decodeHuffman(in, fHuff)-fLav);
				}
			}
		}

		parseEnvelopes();
	}

	void decodeNoise(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
		//select huffman codebooks
		final int[][] tHuff, fHuff;
		final int tLav, fLav;
		if(coupling&&secCh) {
			tHuff = T_HUFFMAN_NOISE_BAL_3_0;
			tLav = T_HUFFMAN_NOISE_BAL_3_0_LAV;
			fHuff = F_HUFFMAN_NOISE_BAL_3_0;
			fLav = F_HUFFMAN_NOISE_BAL_3_0_LAV;
		}
		else {
			tHuff = T_HUFFMAN_NOISE_3_0;
			tLav = T_HUFFMAN_NOISE_3_0_LAV;
			fHuff = F_HUFFMAN_NOISE_3_0;
			fLav = F_HUFFMAN_NOISE_3_0_LAV;
		}

		//read huffman data: i=noise, j=band
		final int noiseBands = tables.getNq();
		final int delta = ((secCh&&coupling) ? 1 : 0)+1;

		int j;
		float[] prev;
		for(int i = 0; i<noiseCount; i++) {
			if(dfNoise[i]) {
				prev = (i==0) ? noiseFDPrevious : noiseFloorData[i-1];
				for(j = 0; j<noiseBands; j++) {
					noiseFloorData[i][j] = prev[j]+delta*(decodeHuffman(in, tHuff)-tLav);
				}
			}
			else {
				noiseFloorData[i][0] = delta*in.readBits(5);
				for(j = 1; j<noiseBands; j++) {
					noiseFloorData[i][j] = noiseFloorData[i][j-1]+delta*(decodeHuffman(in, fHuff)-fLav);
				}
			}
		}

		parseNoise();
	}

	void decodeSinusoidal(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		if(sinusoidalsPresent = in.readBool()) {
			for(int i = 0; i<tables.getN(HIGH); i++) {
				sinusoidals[i] = in.readBool();
			}
		}
	}

	private int decodeHuffman(BitStream in, int[][] table) throws AACException {
		int off = 0;
		int len = table[off][0];
		int cw = in.readBits(len);
		int j;
		while(cw!=table[off][1]) {
			off++;
			j = table[off][0]-len;
			len = table[off][0];
			cw <<= j;
			cw |= in.readBits(j);
		}
		return table[off][2];
	}

	/* ======================= parsing: 4.6.18.3.3 ======================*/
	private void parseEnvelopes() {
		//borders of leading and trailing envelopes
		final int absBordLead, absBordTrail, nRelLead, nRelTrail;
		switch(frameClass) {
			case FIXFIX:
				absBordLead = 0;
				absBordTrail = TIME_SLOTS;
				nRelLead = envCount-1;
				nRelTrail = 0;
				break;
			case FIXVAR:
				absBordLead = 0;
				absBordTrail = varBord1+TIME_SLOTS;
				nRelLead = 0;
				nRelTrail = relCount1;
				break;
			case VARFIX:
				absBordLead = varBord0;
				absBordTrail = TIME_SLOTS;
				nRelLead = relCount0;
				nRelTrail = 0;
				break;
			default:
				//VARVAR
				absBordLead = varBord0;
				absBordTrail = varBord1+TIME_SLOTS;
				nRelLead = relCount0;
				nRelTrail = relCount1;
				break;
		}

		//number of relative borders
		final int[] relBordLead = new int[nRelLead];
		if(frameClass==FIXFIX) Arrays.fill(relBordLead, (int) Math.round((double) TIME_SLOTS/(double) envCount));
		else if(frameClass==VARFIX||frameClass==VARVAR) System.arraycopy(relativeBorders0, 0, relBordLead, 0, nRelLead);

		final int[] relBordTrail = new int[nRelTrail];
		if(frameClass==VARVAR||frameClass==FIXVAR) System.arraycopy(relativeBorders1, 0, relBordTrail, 0, nRelTrail);

		for(int i = 0; i<=envCount; i++) {
			if(i==0) te[i] = absBordLead;
			else if(i==envCount) te[i] = absBordTrail;
			else if(i>=1&&i<=nRelLead) {
				te[i] = absBordLead;
				for(int j = 0; j<i; j++) {
					te[i] += relBordLead[j];
				}
			}
			else if(i>nRelLead&&i<envCount) {
				int sum = 0;
				for(int j = 0; j<envCount-i-1; j++) {
					te[i] += relBordTrail[j];
				}
				te[i] = absBordTrail-sum;
			}
		}
	}

	private void parseNoise() {
		tq[0] = te[0];
		tq[noiseCount] = te[envCount];
		if(envCount==1) tq[1] = te[1];
		else {
			final int middleBorder;
			switch(frameClass) {
				case FIXFIX:
					middleBorder = envCount/2;
					break;
				case VARFIX:
					if(pointer==0) middleBorder = 1;
					else if(pointer==1) middleBorder = envCount-1;
					else middleBorder = pointer-1;
					break;
				default:
					if(pointer>1) middleBorder = envCount+1-pointer;
					else middleBorder = envCount-1;
					break;
			}

			tq[1] = te[middleBorder];
		}
	}

	/* ======================= gets ======================*/
	public float[][] getEnvelopeScalefactors() {
		return envelopeSF;
	}

	public int getEnvCount() {
		return envCount;
	}

	public int[] getTe() {
		return te;
	}

	public float[][] getNoiseFloorData() {
		return noiseFloorData;
	}

	public int getNoiseCount() {
		return noiseCount;
	}

	public int[] getTq() {
		return tq;
	}

	public int[] getFrequencyResolutions() {
		return freqRes;
	}

	int getFrameClass() {
		return frameClass;
	}

	public int getLa(boolean previous) {
		return previous ? laPrevious : la;
	}

	private int getVariableBorder(boolean second) {
		return second ? varBord1 : varBord0;
	}

	private int getRelativeCount(boolean second) {
		return second ? relCount1 : relCount0;
	}

	private int[] getRelativeBorders(boolean second) {
		return second ? relativeBorders1 : relativeBorders0;
	}

	int getPointer() {
		return pointer;
	}

	public boolean areSinusoidalsPresent() {
		return sinusoidalsPresent;
	}

	public boolean[] getSinusoidals() {
		return sinusoidals;
	}

	public boolean[] getSIndexMappedPrevious() {
		return sIndexMappedPrevious;
	}

	void setSIndexMappedPrevious(boolean[][] sIndexMapped) {
		//used by HFAdjuster to save last sIndexMapped for next frame
		this.sIndexMappedPrevious = sIndexMapped[envCountPrev-1];
	}

	int[] getInvfMode(boolean previous) {
		return previous ? invfModePrevious : invfMode;
	}

	float[] getChirpFactors() {
		return bwArray;
	}

	int getNoiseIndex() {
		return noiseIndex;
	}

	void setNoiseIndex(int noiseIndex) {
		this.noiseIndex = noiseIndex;
	}

	int getSineIndex() {
		return sineIndex;
	}

	void setSineIndex(int sineIndex) {
		this.sineIndex = sineIndex;
	}

	/* ======================= copying ======================*/
	void copyGrid(ChannelData cd) {
		frameClass = cd.getFrameClass();
		envCount = cd.getEnvCount();
		noiseCount = cd.getNoiseCount();

		System.arraycopy(cd.getFrequencyResolutions(), 0, freqRes, 0, envCount);

		varBord0 = cd.getVariableBorder(false);
		varBord1 = cd.getVariableBorder(true);
		relCount0 = cd.getRelativeCount(false);
		relCount1 = cd.getRelativeCount(true);

		System.arraycopy(cd.getRelativeBorders(false), 0, relativeBorders0, 0, MAX_RELATIVE_BORDERS);
		System.arraycopy(cd.getRelativeBorders(true), 0, relativeBorders1, 0, MAX_RELATIVE_BORDERS);

		pointer = cd.getPointer();
	}

	void copyInvf(ChannelData cd) {
		System.arraycopy(cd.getInvfMode(false), 0, invfMode, 0, MAX_NQ);
	}
}
