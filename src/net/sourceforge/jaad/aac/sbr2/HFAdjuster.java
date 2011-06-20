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

/*
 * HFAdjustment accoding to 4.6.18.7: Xhigh -> Y
 * 
 * process() calls submethods in following order:
 * 
 * map()				maps dequantized data to eMapped, qMapped, sMapped
 * estimateEnvelopes()	calculates eCurr from Xhigh
 * calculateGain()		calculates Qm, Sm, gain
 * assembleSignals()	assembles everything to Y
 */
class HFAdjuster implements SBRConstants, NoiseTable {

	private static final float[] LIMITER_GAINS = {0.70795f, 1.0f, 1.41254f, 10000000000f};
	private static final float EPSILON = 1.0f;
	private static final float EPSILON_0 = 1e-12f;
	private static final float MAX_BOOST = 1.584893192f;
	private static final float[] SMOOTHING_FACTORS = {
		0.33333333333333f,
		0.30150283239582f,
		0.21816949906249f,
		0.11516383427084f,
		0.03183050093751f
	};
	private static final int[][] PHI = {
		{1, 0, -1, 0},
		{0, 1, 0, -1}
	};
	private static final int MAX_GAIN = 100000;

	private static class Parameter {

		//helper class containing arrays calculated by and passed to different methods
		float[][] eMapped, qMapped;
		boolean[][] sIndexMapped, sMapped;
		float[][] Qm, Sm, Glim;
	}

	public static void process(SBRHeader header, FrequencyTables tables, ChannelData cd, float[][][] Xhigh, float[][][] Y, boolean reset) {
		final Parameter p = map(tables, cd);
		float[][] eCurr = estimateEnvelopes(header, tables, cd, Xhigh);
		calculateGain(header, tables, cd, p, eCurr);
		assembleSignals(header, tables, cd, p, Xhigh, Y, reset);
	}

	//mapping from dequantized values (4.6.18.7.2)
	private static Parameter map(FrequencyTables tables, ChannelData cd) {
		//parameter from FrequencyTables
		final int kx = tables.getKx(false);
		final int[] noiseTable = tables.getNoiseTable();
		final int[] fHigh = tables.getFrequencyTable(HIGH);
		final int nHigh = tables.getN(HIGH);
		final int M = tables.getM(false);

		//parameter from ChannelData
		final int le = cd.getEnvCount();
		final int nq = cd.getNoiseCount();
		final int[] freqRes = cd.getFrequencyResolutions();
		final int la = cd.getLa(false);

		//input and output arrays
		final float[][] eOrig = cd.getEnvelopeScalefactors();
		final float[][] eMapped = new float[le][M];
		final float[][] qOrig = cd.getNoiseFloorData();
		final float[][] qMapped = new float[le][M];
		final boolean[] sinusoidals = cd.getSinusoidals();
		final boolean[] sIndexMappedPrev = cd.getSIndexMappedPrevious();
		final boolean[][] sIndexMapped = new boolean[le][M];
		final boolean[][] sMapped = new boolean[le][M];

		//tmp integer
		int fr, maxI, k, i, m;
		int[] table;

		for(int e = 0; e<le; e++) {
			//envelopes: eOrig -> eMapped
			fr = freqRes[e];
			maxI = tables.getN(fr);
			table = tables.getFrequencyTable(fr);

			for(i = 0; i<maxI; i++) {
				for(m = table[i]; m<table[i+1]; m++) {
					eMapped[e][m-kx] = eOrig[e][i];
				}
			}

			//noise: qOrig -> qMapped
			k = ((cd.getNoiseCount()>1)&&(cd.getTe()[e]>=cd.getTq()[1])) ? 1 : 0;
			for(i = 0; i<nq; i++) {
				for(m = noiseTable[i]; m<noiseTable[i+1]; m++) {
					qMapped[e][m-kx] = qOrig[k][i];
				}
			}

			//sinusoidals: cd.sinusoidals -> sIndexMapped
			for(i = 0; i<nHigh; i++) {
				if(cd.areSinusoidalsPresent()) {
					m = (fHigh[i]+fHigh[i+1])>>1;
					sIndexMapped[e][m-kx] = sinusoidals[i]&&(e>=la||sIndexMappedPrev[m-kx]);
				}
			}
			//sinusoidals: sIndexMapped -> sMapped
			boolean found;
			for(i = 0; i<maxI; i++) {
				found = false;
				for(m = table[i]; !found&&m<table[i+1]; m++) {
					if(sIndexMapped[e][m-kx]) found = true;
				}
				for(m = table[i]; m<table[i+1]; m++) {
					sMapped[e][m-kx] = found;
				}
			}
		}

		cd.setSIndexMappedPrevious(sIndexMapped);

		final Parameter p = new Parameter();
		p.eMapped = eMapped;
		p.qMapped = qMapped;
		p.sIndexMapped = sIndexMapped;
		p.sMapped = sMapped;
		return p;
	}

	//envelope estimation (4.6.18.7.3)
	private static float[][] estimateEnvelopes(SBRHeader header, FrequencyTables tables, ChannelData cd, float[][][] Xhigh) {
		final int[] te = cd.getTe();
		final int M = tables.getM(false);
		final int kx = tables.getKx(false);
		final int le = cd.getEnvCount();

		final float[][] eCurr = new float[le][M];

		float sum;
		int e, m, i, iLow, iHigh;
		if(header.interpolateFrequency()) {
			float div;

			for(e = 0; e<le; e++) {
				div = te[e+1]-te[e];
				iLow = RATE*te[e]+T_HF_ADJ;
				iHigh = RATE*te[e+1]+T_HF_ADJ;

				for(m = 0; m<M; m++) {
					sum = 0.0f;

					//energy = sum over squares of absolute value
					for(i = iLow; i<iHigh; i++) {
						sum += Xhigh[m+kx][i][0]*Xhigh[m+kx][i][0]+Xhigh[m+kx][i][1]*Xhigh[m+kx][i][1];
					}
					eCurr[e][m] = sum/div;
				}
			}
		}
		else {
			final int[] n = tables.getN();
			final int[] freqRes = cd.getFrequencyResolutions();

			int k;
			int[] table;
			int div1, div2;

			for(e = 0; e<le; e++) {
				div1 = RATE*(te[e+1]-te[e]);
				iLow = RATE*te[e]+T_HF_ADJ;
				iHigh = RATE*te[e+1]+T_HF_ADJ;
				table = tables.getFrequencyTable(freqRes[e+1]);

				for(m = 0; m<n[freqRes[e+1]]; m++) {
					sum = 0.0f;
					div2 = div1*(table[m+1]-table[m]);

					for(k = table[m]; k<table[m+1]; k++) {
						for(i = iLow; i<iHigh; i++) {
							sum += Xhigh[k][i][0]*Xhigh[k][i][0]+Xhigh[k][i][1]*Xhigh[k][i][1];
						}
					}
					sum /= div2;
					for(k = table[m]; k<table[m+1]; k++) {
						eCurr[e][k-kx] = sum;
					}
				}
			}
		}

		return eCurr;
	}

	//calculation of levels of additional HF signal components (4.6.18.7.4) and gain calculation (4.6.18.7.5)
	private static void calculateGain(SBRHeader header, FrequencyTables tables, ChannelData cd, Parameter p, float[][] eCurr) {
		final int limGain = header.getLimiterGains();
		final int M = tables.getM(false);
		final int nl = tables.getNl();
		final int[] fLim = tables.getLimiterTable();
		final int kx = tables.getKx(false);

		final int la = cd.getLa(false);
		final int laPrevious = cd.getLa(true);
		final int le = cd.getEnvCount();

		//output arrays
		final float[][] Qm = new float[le][M];
		final float[][] Sm = new float[le][M];
		final float[][] gain = new float[le][M];

		boolean delta, delta2;
		int m, k, i;
		final int[] km = new int[M];
		final float[] eMappedSum = new float[nl];
		float tmp;
		final float[][] gTemp = new float[le][nl];
		float gMax;

		//TODO: optimize this loops
		for(int e = 0; e<le; e++) {
			delta = (e!=la)&&(e!=laPrevious);

			for(m = 0; m<M; m++) {
				tmp = p.eMapped[e][m]/(1.0f+p.qMapped[e][m]);
				Qm[e][m] = (float) Math.sqrt(tmp*p.qMapped[e][m]);
				Sm[e][m] = p.sIndexMapped[e][m] ? (float) Math.sqrt(tmp) : 0;

				//TODO: is epsilon==1.0f ???
				if(p.sMapped[e][m]) {
					gain[e][m] = (float) Math.sqrt(p.eMapped[e][m]*p.qMapped[e][m]
							/((EPSILON+eCurr[e][m])*(1.0f+p.qMapped[e][m])));
				}
				else {
					gain[e][m] = (float) Math.sqrt(p.eMapped[e][m]
							/((EPSILON+eCurr[e][m])*(1.0f+(delta ? p.qMapped[e][m] : 0))));
				}
			}

			for(k = 0; k<nl; k++) {
				eMappedSum[k] = EPSILON_0;
				tmp = EPSILON_0;
				for(i = fLim[k]-kx; i<fLim[k+1]-kx; i++) {
					eMappedSum[k] += p.eMapped[e][i];
					tmp += eCurr[e][i];
				}
				gTemp[e][k] = (float) Math.sqrt(eMappedSum[k]/tmp)*LIMITER_GAINS[limGain];
			}

			for(m = 0; m<M; m++) {
				km[m] = -1;
				for(i = 0; km[m]<0&&i<fLim.length; i++) {
					if(fLim[i]<=(m+kx)&&fLim[i+1]>(m+kx)) km[m] = i;
				}
				gMax = Math.min(gTemp[e][km[m]], MAX_GAIN);

				Qm[e][m] = Math.min(Qm[e][m], Qm[e][m]*(gMax/gain[e][m]));
				gain[e][m] = Math.min(gain[e][m], gMax);
			}

			for(k = 0; k<nl; k++) {
				tmp = EPSILON_0;
				for(i = fLim[k]-kx; i<fLim[k+1]-kx; i++) {
					delta2 = Sm[e][i]==0&&delta;
					tmp += (eCurr[e][i]*gain[e][i]*gain[e][i])
							+(Sm[e][i]*Sm[e][i])
							+(delta2 ? (Qm[e][i]*Qm[e][i]) : 0);
				}
				gTemp[e][k] = (float) Math.sqrt(eMappedSum[k]/tmp);
			}

			//apply boost
			for(m = 0; m<M; m++) {
				gMax = Math.min(gTemp[e][km[m]], MAX_BOOST);
				Qm[e][m] *= gMax;
				Sm[e][m] *= gMax;
			}
		}

		p.Qm = Qm;
		p.Sm = Sm;
		p.Glim = gain;
	}

	//assembling HF signals (4.6.18.7.5)
	private static void assembleSignals(SBRHeader header, FrequencyTables tables, ChannelData cd, Parameter p, float[][][] Xhigh, float[][][] Y, boolean reset) {
		final int hSL = header.isSmoothingMode() ? 0 : 4;
		final int M = tables.getM(false);
		final int le = cd.getEnvCount();
		final int lePrev = cd.getEnvCountPrevious();
		final int[] te = cd.getTe();
		final int la = cd.getLa(false);
		final int laPrev = cd.getLa(true);
		final int kx = tables.getKx(false);
		int noiseIndex = reset ? 0 : cd.getNoiseIndex();
		int sineIndex = cd.getSineIndex();

		final float[][] gTmp = cd.getGTmp();
		final float[][] qTmp = cd.getQTmp();
		final float[][][] gFilt = new float[42][M][2]; //TODO: size
		final float[][][] qFilt = new float[42][M][2]; //TODO: size

		int e, i, m, j;

		//save previous values
		if(reset) {
			for(i = 0; i<hSL; i++) {
				System.arraycopy(gTmp[lePrev-hSL+i], 0, p.Glim[0], 0, M);
				System.arraycopy(qTmp[lePrev-hSL+i], 0, p.Qm[0], 0, M);
			}
		}
		else if(hSL!=0) {
			for(i = 0; i<hSL; i++) {
				System.arraycopy(gTmp[lePrev-hSL+i], 0, gTmp[RATE*te[i]], 0, M);
				System.arraycopy(qTmp[lePrev-hSL+i], 0, qTmp[RATE*te[i]], 0, M);
			}
		}

		//calculate new
		int phiSign = (1-2*(kx&1));

		for(e = 0; e<le; e++) {
			//fill gTmp and qTmp
			for(i = RATE*te[e]; i<RATE*te[e+1]; i++) {
				System.arraycopy(p.Glim[e], 0, gTmp[i+hSL], 0, M);
				System.arraycopy(p.Qm[e], 0, qTmp[i+hSL], 0, M);
			}

			//fill gFilt = W1
			if((e!=la)&&(e!=laPrev)&&(hSL!=0)) {
				for(i = RATE*te[e]; i<RATE*te[e+1]; i++) {
					for(m = 0; m<M; m++) {
						for(j = 0; j<hSL; j++) {
							gFilt[i][m][0] += gTmp[i-j+hSL][m]*SMOOTHING_FACTORS[j];
						}
						gFilt[i][m][1] = gFilt[i][m][0]*Xhigh[m+kx][i+T_HF_ADJ][1];
						gFilt[i][m][0] *= Xhigh[m+kx][i+T_HF_ADJ][0];
					}
				}
			}
			else {
				for(i = RATE*te[e]; i<RATE*te[e+1]; i++) {
					for(m = 0; m<M; m++) {
						gFilt[i][m][0] = gTmp[i+hSL][m]*Xhigh[m+kx][i+T_HF_ADJ][0];
						gFilt[i][m][1] = gTmp[i+hSL][m]*Xhigh[m+kx][i+T_HF_ADJ][1];
					}
				}
			}

			//fill qFilt = W2
			for(i = RATE*te[e]; i<RATE*te[e+1]; i++) {
				for(m = 0; m<M; m++) {
					noiseIndex = (noiseIndex+1)&0x1FF;
					if((e!=la)&&(e!=laPrev)&&(p.Sm[e][m]==0)) {
						if(hSL==0) gFilt[i][m][0] = qTmp[i][m];
						else {
							qFilt[i][m][0] = 0;
							for(j = 0; j<hSL; j++) {
								qFilt[i][m][0] += qTmp[i-j-hSL][m]*SMOOTHING_FACTORS[j];
							}
						}
						qFilt[i][m][1] = gFilt[i][m][1]+qFilt[i][m][0]*NOISE_TABLE[noiseIndex][1];
						qFilt[i][m][0] = gFilt[i][m][0]+qFilt[i][m][0]*NOISE_TABLE[noiseIndex][0];
					}
					else {
						qFilt[i][m][0] = gFilt[i][m][0];
						qFilt[i][m][1] = gFilt[i][m][1];
					}
				}
			}

			//fill Y
			for(i = RATE*te[e]; i<RATE*te[e+1]; i++) {
				for(m = 0; m<M; m++) {
					Y[i+T_HF_ADJ][m+kx][0] = qFilt[i][m][0]+(p.Sm[e][m]*PHI[0][sineIndex]);
					Y[i+T_HF_ADJ][m+kx][1] = qFilt[i][m][1]+(p.Sm[e][m]*PHI[1][sineIndex]*phiSign);
					phiSign = -phiSign;
				}
				sineIndex = (sineIndex+1)&3;
			}
		}

		cd.setNoiseIndex(noiseIndex);
		cd.setSineIndex(sineIndex);
	}
}
