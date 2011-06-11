package net.sourceforge.jaad.aac.sbr2;

import java.util.Arrays;

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

	private static final float[] GAIN_LIMITS = {0.70795f, 1.0f, 1.41254f, 10000000000f};
	private static final float EPSILON = 1e-12f;
	private static final float BOOST_FACTOR = 1.584893192f;
	private static final float[] SMOOTHING_FACTORS = {
		0.33333333333333f,
		0.30150283239582f,
		0.21816949906249f,
		0.11516383427084f,
		0.03183050093751f
	};
	private static final int[][] PHI = {
		{1, 0, -1, 0},
		{0, 1, 0, -1},
	};

	private static class Parameter {

		//helper class containing arrays calculated by and passed to different methods
		float[][] eMapped, qMapped;
		boolean[][] sIndexMapped, sMapped;
		float[][] eCurr;
		float[][] Qm, Sm, gain;
	}

	public static void process(SBRHeader header, FrequencyTables tables, ChannelData cd, float[][][] Xhigh, float[][][] Y) {
		final Parameter p = map(tables, cd);
		p.eCurr = estimateEnvelopes(header, tables, cd, Xhigh);
		calculateGain(header, tables, cd, p);
		assembleSignals(header, tables, cd, p, Xhigh, Y);
	}

	//mapping from dequantized values (4.6.18.7.2)
	private static Parameter map(FrequencyTables tables, ChannelData cd) {
		//parameter from FrequencyTables
		final int kx = tables.getKx(false);
		final int[] noiseTable = tables.getNoiseTable();
		final int[] fHigh = tables.getFrequencyTable(HIGH);
		final int maxM = (fHigh[tables.getN(HIGH)-1]+fHigh[tables.getN(HIGH)])>>1;

		//parameter from ChannelData
		final int le = cd.getEnvCount();
		final int nq = cd.getNoiseCount();
		final int[] freqRes = cd.getFrequencyResolutions();
		final int la = cd.getLa(false);

		//input and output arrays
		final float[][] envFS = cd.getEnvelopeScalefactors();
		final float[][] eMapped = new float[le][];
		final float[][] noiseFD = cd.getNoiseFloorData();
		final float[][] qMapped = new float[le][];
		final boolean[] sinusoidals = cd.getSinusoidals();
		final boolean[] sIndexMappedPrev = cd.getSIndexMappedPrevious();
		final boolean[][] sIndexMapped = new boolean[le][];
		final boolean[][] sMapped = new boolean[le][];

		//tmp integer
		int fr, maxI, k, i, m;
		int[] table;

		for(int e = 0; e<le; e++) {
			//envelopes: envFS -> eMapped
			fr = freqRes[e];
			maxI = tables.getN(fr);
			table = tables.getFrequencyTable(fr);

			eMapped[e] = new float[table[maxI]-kx];
			for(i = 0; i<maxI; i++) {
				for(m = table[i]; m<table[i+1]; m++) {
					eMapped[e][m-kx] = envFS[e+1][i];
				}
			}

			//noise: noiseFD -> qMapped
			k = ((cd.getNoiseCount()>1)&&(cd.getTe()[e]>=cd.getTq()[1])) ? 1 : 0;
			qMapped[e] = new float[noiseTable[nq]-kx];
			for(i = 0; i<nq; i++) {
				for(m = noiseTable[i]; m<noiseTable[i+1]; m++) {
					qMapped[e][m-kx] = noiseFD[k+1][i];
				}
			}

			//sinusoidals: cd.sinusoidals -> sIndexMapped
			sIndexMapped[e] = new boolean[maxM-kx];
			for(i = 0; i<tables.getN(HIGH); i++) {
				if(cd.areSinusoidalsPresent()) {
					m = (fHigh[i]+fHigh[i+1])>>1;
					sIndexMapped[e][m-kx] = sinusoidals[i]&&(e>=la||(sIndexMappedPrev[m-kx]));
				}
			}
			//sinusoidals: sIndexMapped -> sMapped
			final int max = (table[maxI-1]-kx)+(table[maxI]-table[maxI-1]);
			sMapped[e] = new boolean[max];
			for(i = 0; i<maxI; i++) {
				boolean found = false;
				for(m = table[i]; !found&&m<table[i+1]; m++) {
					if(sIndexMapped[e+1][m-kx]) found = true;
				}
				final int from = table[i]-kx;
				Arrays.fill(sMapped[e], from, from+(table[i+1]-table[i]), found);
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
		final int M = tables.getM();
		final int kx = tables.getKx(false);

		final float[][] eCurr = new float[cd.getEnvCount()][M];

		float sum;
		int e, m, i, iLow, iHigh;
		if(header.interpolateFrequency()) {
			float size;

			for(e = 0; e<cd.getEnvCount(); e++) {
				size = 0.5f/(te[e+1]-te[e]);
				iLow = te[e]*2+T_HF_ADJ;
				iHigh = te[e+1]*2+T_HF_ADJ;

				for(m = 0; m<M; m++) {
					sum = 0.0f;

					//energy = sum over squares of absolute value
					for(i = iLow; i<iHigh; i++) {
						sum += Xhigh[m+kx][i][0]*Xhigh[m+kx][i][0]+Xhigh[m+kx][i][1]*Xhigh[m+kx][i][1];
					}
					eCurr[e][m] = sum*size;
				}
			}
		}
		else {
			final int[] n = tables.getN();
			final int[] freqRes = cd.getFrequencyResolutions();

			int k, size;
			int[] table;

			for(e = 0; e<cd.getEnvCount(); e++) {
				size = 2*(te[e+1]-te[e]);
				iLow = te[e]*2+T_HF_ADJ;
				iHigh = te[e+1]*2+T_HF_ADJ;
				table = tables.getFrequencyTable(freqRes[e+1]);

				for(m = 0; m<n[freqRes[e+1]]; m++) {
					sum = 0.0f;
					final int den = size*(table[m+1]-table[m]);

					for(k = table[m]; k<table[m+1]; k++) {
						for(i = iLow; i<iHigh; i++) {
							sum += Xhigh[k][i][0]*Xhigh[k][i][0]+Xhigh[k][i][1]*Xhigh[k][i][1];
						}
					}
					sum /= den;
					for(k = table[m]; k<table[m+1]; k++) {
						eCurr[e][k-kx] = sum;
					}
				}
			}
		}

		return eCurr;
	}

	//calculation of levels of additional HF signal components (4.6.18.7.4) and gain calculation (4.6.18.7.5)
	private static void calculateGain(SBRHeader header, FrequencyTables tables, ChannelData cd, Parameter p) {
		//parameter from header
		final int limGains = header.getLimiterGains();
		//parameter from FrequencyTables
		final int[] fLim = tables.getLimiterTable();
		final int kx = tables.getKx(false);

		//parameter from ChannelData
		final int la = cd.getLa(false);
		final int laPrevious = cd.getLa(true);
		final int le = cd.getEnvCount();

		//output arrays
		final int max = fLim[tables.getNl()+1]-kx;
		final float[][] Qm = new float[le][max];
		final float[][] Sm = new float[le][max];
		final float[][] gain = new float[le][max];

		int k, m, delta;
		float gainBoost, gainMax, tmp;
		final float[] sum = new float[2];

		for(int e = 0; e<le; e++) {
			delta = ((e==la)||(e==laPrevious)) ? 0 : 1;

			for(k = 0; k<tables.getNl(); k++) {
				sum[0] = 0;
				sum[1] = 0;

				//calculate Qm, Sm and gain
				for(m = fLim[k]-kx; m<fLim[k+1]-kx; m++) {
					tmp = p.eMapped[e][m]/(1.0f+p.qMapped[e][m]);
					Qm[e][m] = (float) Math.sqrt(tmp*p.qMapped[e][m]);
					Sm[e][m] = (float) Math.sqrt(tmp*(p.sIndexMapped[e+1][m] ? 1 : 0));
					if(p.sMapped[e][m]) {
						gain[e][m] = (float) Math.sqrt(p.eMapped[e][m]*p.qMapped[e][m]
								/((1.0f+p.eCurr[e][m])
								*(1.0f+p.qMapped[e][m])));
					}
					else {
						gain[e][m] = (float) Math.sqrt(p.eMapped[e][m]
								/((1.0f+p.eCurr[e][m])
								*(1.0f+p.qMapped[e][m]*delta)));
					}
				}

				//calculate max values
				for(m = fLim[k]-kx; m<fLim[k+1]-kx; m++) {
					sum[0] += p.eMapped[e][m];
					sum[1] += p.eCurr[e][m];
				}
				gainMax = GAIN_LIMITS[limGains]*(float) Math.sqrt((EPSILON+sum[0])/(EPSILON+sum[1]));
				gainMax = Math.min(100000.f, gainMax);
				//check max values
				float qMax;
				for(m = fLim[k]-kx; m<fLim[k+1]-kx; m++) {
					qMax = Qm[e][m]*gainMax/gain[e][m];
					Qm[e][m] = Math.min(Qm[e][m], qMax);
					gain[e][m] = Math.min(gain[e][m], gainMax);
				}

				//calculate boost
				sum[0] = sum[1] = 0.0f;
				for(m = fLim[k]-kx; m<fLim[k+1]-kx; m++) {
					sum[0] += p.eMapped[e][m];
					sum[1] += p.eCurr[e][m]*gain[e][m]*gain[e][m]
							+Sm[e][m]*Sm[e][m]
							+((delta!=0&&Sm[e][m]==0) ? 1 : 0)*Qm[e][m]*Qm[e][m];
				}
				gainBoost = (float) Math.sqrt((EPSILON+sum[0])/(EPSILON+sum[1]));
				gainBoost = Math.min(BOOST_FACTOR, gainBoost);
				//apply boost
				for(m = fLim[k]-kx; m<fLim[k+1]-kx; m++) {
					gain[e][m] *= gainBoost;
					Qm[e][m] *= gainBoost;
					Sm[e][m] *= gainBoost;
				}
			}
		}

		p.Qm = Qm;
		p.Sm = Sm;
		p.gain = gain;
	}

	//assembling HF signals (4.6.18.7.5)
	private static void assembleSignals(SBRHeader header, FrequencyTables tables, ChannelData cd, Parameter p, float[][][] Xhigh, float[][][] Y) {
		final int hSL = header.isSmoothingMode() ? 0 : 4;
		final int kx = tables.getKx(false);
		final int mMax = tables.getM();

		float[][] gTmp = new float[48][];
		float[][] q_temp = new float[48][];
		int noiseIndex = cd.getNoiseIndex();
		int sineIndex = cd.getSineIndex();

		/*if (sbr->reset) {
		for (i = 0; i < h_SL; i++) {
		memcpy(g_temp[i + 2*ch_data->t_env[0]], sbr->gain[0], m_max * sizeof(sbr->gain[0][0]));
		memcpy(q_temp[i + 2*ch_data->t_env[0]], sbr->q_m[0],  m_max * sizeof(sbr->q_m[0][0]));
		}
		} else if (hSL!=0) {
		memcpy(g_temp[2*ch_data->t_env[0]], g_temp[2*ch_data->t_env_num_env_old], 4*sizeof(g_temp[0]));
		memcpy(q_temp[2*ch_data->t_env[0]], q_temp[2*ch_data->t_env_num_env_old], 4*sizeof(q_temp[0]));
		}*/

		final int envCount = cd.getEnvCount();
		final int[] te = cd.getTe();
		final int la = cd.getLa(false);
		final int laPrevious = cd.getLa(true);

		int e, i;
		for(e = 0; e<envCount; e++) {
			for(i = 2*te[e]; i<2*te[e+1]; i++) {
				System.arraycopy(p.gain[e], 0, gTmp[hSL+i], 0, mMax);
				//memcpy(g_temp[h_SL + i], sbr->gain[e], m_max * sizeof(sbr->gain[0][0]));
				System.arraycopy(p.Qm[e], 0, q_temp[hSL+i], 0, mMax);
				//memcpy(q_temp[h_SL + i], sbr->q_m[e],  m_max * sizeof(sbr->q_m[0][0]));
			}
		}

		int phiSign, m, j;
		for(e = 0; e<envCount; e++) {
			for(i = 2*te[e]; i<2*te[e+1]; i++) {
				phiSign = (1-2*(kx&1));

				//gain
				if(hSL!=0&&e!=laPrevious&&e!=la) {
					for(m = 0; m<mMax; m++) {
						final int idx1 = i+hSL;
						float gFilt = 0.0f;
						for(j = 0; j<=hSL; j++) {
							gFilt += gTmp[idx1-j][m]*SMOOTHING_FACTORS[j];
						}
						Y[i][m+kx][0] = Xhigh[m+kx][i+T_HF_ADJ][0]*gFilt;
						Y[i][m+kx][1] = Xhigh[m+kx][i+T_HF_ADJ][1]*gFilt;
					}
				}
				else {
					for(m = 0; m<mMax; m++) {
						final float gFilt = gTmp[i+hSL][m];
						Y[i][m+kx][0] = Xhigh[m+kx][i+T_HF_ADJ][0]*gFilt;
						Y[i][m+kx][1] = Xhigh[m+kx][i+T_HF_ADJ][1]*gFilt;
					}
				}

				//noise
				if(e!=laPrevious&&e!=la) {
					for(m = 0; m<mMax; m++) {
						noiseIndex = (noiseIndex+1)&0x1ff;
						if(p.Sm[e][m]!=0) {
							Y[i][m+kx][0] += p.Sm[e][m]*PHI[0][sineIndex];
							Y[i][m+kx][1] += p.Sm[e][m]*(PHI[1][sineIndex]*phiSign);
						}
						else {
							float qFilt;
							if(hSL!=0) {
								final int idx1 = i+hSL;
								qFilt = 0.0f;
								for(j = 0; j<=hSL; j++) {
									qFilt += q_temp[idx1-j][m]*SMOOTHING_FACTORS[j];
								}
							}
							else qFilt = q_temp[i][m];

							Y[i][m+kx][0] += qFilt*NOISE_TABLE[noiseIndex][0];
							Y[i][m+kx][1] += qFilt*NOISE_TABLE[noiseIndex][1];
						}
						phiSign = -phiSign;
					}
				}
				else {
					noiseIndex = (noiseIndex+mMax)&0x1ff;
					for(m = 0; m<mMax; m++) {
						Y[i][m+kx][0] += p.Sm[e][m]*PHI[0][sineIndex];
						Y[i][m+kx][1] += p.Sm[e][m]*(PHI[1][sineIndex]*phiSign);
						phiSign = -phiSign;
					}
				}
				sineIndex = (sineIndex+1)&3;
			}
		}
		cd.setNoiseIndex(noiseIndex);
		cd.setSineIndex(sineIndex);
	}
}
