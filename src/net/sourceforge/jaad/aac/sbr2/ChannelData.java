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

	private static final int[] POW2_TABLE = {1, 2, 4, 8};
	//grid
	private int frameClass;
	private int envCount, envCountPrev, noiseCount;
	private int[] freqRes;
	private int freqResPrevious;
	private int varBord0, varBord1;
	private int relCount0, relCount1;
	private int[] relativeBorders0, relativeBorders1;
	private int pointer;
	private int la, laPrevious;
	//dtdf
	private boolean[] dfEnv, dfNoise;
	//invf
	private int[] invfMode, invfModePrevious;
	//envelopes
	private float[][] envelopeSF;
	private float[] envelopeSFPrevious;
	private int[] te; //envelope time borders, p.214
	private float[][] eMapped;
	//noise
	private float[][] noiseFloorData;
	private float[] noiseFDPrevious; //last of previous frame
	private int[] tq; //noise floor time borders, p.215
	//sinusoidal
	private boolean sinusoidalsPresent;
	private boolean[] sinusoidals;
	private boolean[] sIndexMappedPrevious;
	//chirp factors (calculated by HFGenerator)
	private float[] bwArray;
	//indizes for assembling in HFAdjustment
	private int noiseIndex, sineIndex;

	ChannelData() {
		freqRes = new int[]{0};
		invfMode = new int[0];
		envelopeSF = new float[1][0];
		noiseFloorData = new float[1][0];
		relativeBorders0 = new int[0];
		relativeBorders1 = new int[0];
	}

	/* ======================= decoding ======================*/
	void decodeGrid(BitStream in, SBRHeader header) throws AACException {
		final int bits;
		//save previous
		freqResPrevious = freqRes[freqRes.length-1];
		envCountPrev = envCount;

		switch(frameClass = in.readBits(2)) {
			case FIXFIX:
				envCount = POW2_TABLE[in.readBits(2)];
				if(envCount==1) header.setAmpRes(false);
				//check requirement (4.6.18.6.3):
				else if(envCount>4) throw new AACException("SBR: too many envelopes: "+envCount);

				freqRes = new int[envCount];
				Arrays.fill(freqRes, in.readBit());
				break;
			case FIXVAR:
				varBord1 = in.readBits(2);
				relCount1 = in.readBits(2);
				envCount = relCount1+1;

				relativeBorders1 = new int[relCount1];
				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/LOG2); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
				}
				break;
			case VARFIX:
				varBord0 = in.readBits(2);
				relCount0 = in.readBits(2);
				envCount = relCount0+1;

				relativeBorders0 = new int[relCount0];
				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/Math.log(2)); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
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

				relativeBorders0 = new int[relCount0];
				for(int i = 0; i<relCount0; i++) {
					relativeBorders0[i] = 2*in.readBits(2)+2;
				}

				relativeBorders1 = new int[relCount1];
				for(int i = 0; i<relCount1; i++) {
					relativeBorders1[i] = 2*in.readBits(2)+2;
				}

				bits = (int) Math.ceil(Math.log(envCount+1)/Math.log(2)); //TODO: replace with table
				pointer = in.readBits(bits);

				freqRes = new int[envCount];
				for(int i = 0; i<envCount; i++) {
					freqRes[envCount-1-i] = in.readBit();
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
		dfEnv = new boolean[envCount];
		for(int i = 0; i<envCount; i++) {
			dfEnv[i] = in.readBool();
		}

		dfNoise = new boolean[noiseCount];
		for(int i = 0; i<noiseCount; i++) {
			dfNoise[i] = in.readBool();
		}
	}

	void decodeInvf(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		invfModePrevious = invfMode;

		invfMode = new int[tables.getNq()];
		for(int i = 0; i<invfMode.length; i++) {
			invfMode[i] = in.readBits(2);
		}
	}

	void decodeEnvelope(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
		//save previous
		envelopeSFPrevious = envelopeSF[envelopeSF.length-1];

		final boolean ampRes = header.getAmpRes();

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
		envelopeSF = new float[envCount][];
		final int[] envBands = tables.getN();
		final int bits = 7-((coupling&&secCh) ? 1 : 0)-(ampRes ? 1 : 0);
		final int delta = (secCh&&coupling) ? 1 : 0+1;
		final int odd = envBands[1]&1;

		int j, k, frPrev;
		float[] prev;
		for(int i = 0; i<envCount; i++) {
			envelopeSF[i] = new float[envBands[freqRes[i]]];
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
		//save previous
		noiseFDPrevious = noiseFloorData[noiseFloorData.length-1];

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
		final int delta = (secCh&&coupling) ? 1 : 0+1;
		noiseFloorData = new float[noiseCount][noiseBands];

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
			sinusoidals = new boolean[tables.getN(HIGH)];
			for(int i = 0; i<sinusoidals.length; i++) {
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

		te = new int[envCount+1];
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
		if(envCount==1) tq = new int[]{te[0], te[1]};
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

			tq = new int[]{te[0], te[middleBorder], te[envCount]};
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

	void setChirpFactors(float[] bwArray) {
		//used by HFGenerator after calculating chirp factors
		this.bwArray = bwArray;
	}

	float[][] getEMapped() {
		return eMapped;
	}

	void setEMapped(float[][] eMapped) {
		//used by HFAdjuster after mapping
		this.eMapped = eMapped;
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
		//save previous
		freqResPrevious = freqRes[freqRes.length-1];

		//copy
		frameClass = cd.getFrameClass();
		envCount = cd.getEnvCount();
		noiseCount = cd.getNoiseCount();

		freqRes = Arrays.copyOf(cd.getFrequencyResolutions(), cd.getFrequencyResolutions().length);

		varBord0 = cd.getVariableBorder(false);
		varBord1 = cd.getVariableBorder(true);
		relCount0 = cd.getRelativeCount(false);
		relCount1 = cd.getRelativeCount(true);

		relativeBorders0 = Arrays.copyOf(cd.getRelativeBorders(false), cd.getRelativeBorders(false).length);
		relativeBorders1 = Arrays.copyOf(cd.getRelativeBorders(true), cd.getRelativeBorders(true).length);

		pointer = cd.getPointer();
	}

	void copyInvf(ChannelData cd) {
		//save previous
		invfModePrevious = invfMode;

		//copy
		invfMode = Arrays.copyOf(cd.getInvfMode(false), cd.getInvfMode(false).length);
	}
}
