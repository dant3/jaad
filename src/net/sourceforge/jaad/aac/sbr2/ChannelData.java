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
	private int envCount, noiseCount;
	private int[] freqRes;
	private int freqResPrevious;
	private int varBord0, varBord1;
	private int relCount0, relCount1;
	private int[] relativeBorders0, relativeBorders1;
	private int pointer;
	//dtdf
	private boolean[] dfEnv, dfNoise;
	//invf
	private int[] invfMode, invfModePrevious;
	//envelopes
	private int[][] envelopeData;
	private int[] envelopeDataPrevious;
	private int[] te; //envelope time borders, p.214
	private double[][] envelopeScalefactors; //delta decoded, p.216
	//noise
	private int[][] noiseData;
	private int[] noiseDataPrevious; //last of previous frame
	private int[] tq; //noise floor time borders, p.215
	private double[][] noiseFloorData; //delta decoded, p.217
	//sinusoidal
	private boolean harmonicPresent; //TODO: is this flag needed?
	private boolean[] harmonic;
	//chirp factors (calculated by HFGenerator)
	private float[] bwArray;

	ChannelData() {
		freqRes = new int[]{0};
		invfMode = new int[0];
		envelopeData = new int[1][0];
		noiseData = new int[][]{{0}};
		relativeBorders0 = new int[0];
		relativeBorders1 = new int[0];
	}

	/* ======================= decoding ======================*/
	void decodeGrid(BitStream in, SBRHeader header) throws AACException {
		final int bits;
		//save previous
		freqResPrevious = freqRes[freqRes.length-1];
		invfModePrevious = invfMode;
		envelopeDataPrevious = envelopeData[envelopeData.length-1];
		noiseDataPrevious = noiseData[noiseData.length-1];

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
		invfMode = new int[tables.getNq()];
		for(int i = 0; i<invfMode.length; i++) {
			invfMode[i] = in.readBits(2);
		}
	}

	void decodeEnvelope(BitStream in, SBRHeader header, FrequencyTables tables, boolean secCh, boolean coupling) throws AACException {
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
		envelopeData = new int[envCount][];
		final int[] envBands = tables.getN();
		final int bits = 7-((coupling&&secCh) ? 1 : 0)-(ampRes ? 1 : 0);
		final int delta = (secCh&&coupling) ? 1 : 0+1;
		final int odd = envBands[1]&1;

		int j, k;
		int[] prev;
		for(int i = 0; i<envCount; i++) {
			envelopeData[i] = new int[envBands[freqRes[i]]];
			prev = (i==0) ? envelopeDataPrevious : envelopeData[i-1];

			if(dfEnv[i]) {
				if(freqRes[i]==freqRes[i-1]) {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						envelopeData[i][j] = prev[j]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
				else if(freqRes[i]==1) {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						k = (j+odd)>>1; //fLow[k] <= fHigh[j] < fLow[k + 1]
						envelopeData[i][j] = prev[k]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
				else {
					for(j = 0; j<envBands[freqRes[i]]; j++) {
						k = (j!=0) ? (2*j-odd) : 0; //fHigh[k] == fLow[j]
						envelopeData[i][j] = prev[k]+delta*(decodeHuffman(in, tHuff)-tLav);
					}
				}
			}
			else {
				envelopeData[i][0] = delta*in.readBits(bits);
				for(j = 1; j<envBands[freqRes[i]]; j++) {
					envelopeData[i][j] = envelopeData[i][j-1]+delta*(decodeHuffman(in, fHuff)-fLav);
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
		final int delta = (secCh&&coupling) ? 1 : 0+1;
		noiseData = new int[noiseCount][noiseBands];

		int j;
		int[] prev;
		for(int i = 0; i<noiseCount; i++) {
			if(dfNoise[i]) {
				prev = (i==0) ? noiseDataPrevious : noiseData[i-1];
				for(j = 0; j<noiseBands; j++) {
					noiseData[i][j] = prev[j]+delta*(decodeHuffman(in, tHuff)-tLav);
				}
			}
			else {
				noiseData[i][0] = delta*in.readBits(5);
				for(j = 1; j<noiseBands; j++) {
					noiseData[i][j] = noiseData[i][j-1]+delta*(decodeHuffman(in, fHuff)-fLav);
				}
			}
		}

		parseNoise();
	}

	void decodeSinusoidal(BitStream in, SBRHeader header, FrequencyTables tables) throws AACException {
		if(harmonicPresent = in.readBool()) {
			harmonic = new boolean[tables.getN(HIGH)];
			for(int i = 0; i<harmonic.length; i++) {
				harmonic[i] = in.readBool();
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

	private void deltaDecodeNoise(FrequencyTables tables, boolean secAndCoupling) {
		final int delta = secAndCoupling ? 2 : 1;
		noiseFloorData = new double[tables.getNq()][noiseCount];
		int prev;
		for(int l = 0; l<noiseCount; l++) {
			for(int k = 0; k<noiseFloorData.length; k++) {
				if(dfNoise[l]) {
					prev = (l==0) ? noiseDataPrevious[k] : noiseData[l-1][k];
					noiseFloorData[k][l] = prev+delta*noiseData[l][k];
				}
				else {
					noiseFloorData[k][l] = 0;
					for(int i = 0; i<k; i++) {
						noiseFloorData[k][l] += (delta*noiseData[l][i]);
					}
				}
			}
		}
	}

	/* ======================= gets ======================*/
	public double[][] getEnvelopeScalefactors() {
		return envelopeScalefactors;
	}

	public int getEnvCount() {
		return envCount;
	}

	public double[][] getNoiseFloorData() {
		return noiseFloorData;
	}

	public int getNoiseCount() {
		return noiseCount;
	}

	public int[] getFrequencyResolutions() {
		return freqRes;
	}

	private int getFrameClass() {
		return frameClass;
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

	private int getPointer() {
		return pointer;
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

	/* ======================= copying ======================*/
	void copyGrid(ChannelData cd) {
		frameClass = cd.getFrameClass();
		envCount = cd.getEnvCount();
		noiseCount = cd.getNoiseCount();

		int[] tmp = cd.getFrequencyResolutions();
		freqRes = new int[tmp.length];
		System.arraycopy(tmp, 0, freqRes, 0, tmp.length);

		varBord0 = cd.getVariableBorder(false);
		varBord1 = cd.getVariableBorder(true);
		relCount0 = cd.getRelativeCount(false);
		relCount1 = cd.getRelativeCount(true);

		tmp = cd.getRelativeBorders(false);
		relativeBorders0 = new int[tmp.length];
		System.arraycopy(tmp, 0, relativeBorders0, 0, tmp.length);

		tmp = cd.getRelativeBorders(true);
		relativeBorders1 = new int[tmp.length];
		System.arraycopy(tmp, 0, relativeBorders1, 0, tmp.length);

		pointer = cd.getPointer();
	}

	void copyInvf(ChannelData cd) {
		final int[] tmp = cd.getInvfMode(false);
		invfMode = new int[tmp.length];
		System.arraycopy(tmp, 0, invfMode, 0, tmp.length);
	}
}
