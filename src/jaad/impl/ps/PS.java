package jaad.impl.ps;

import jaad.AACException;
import jaad.impl.BitStream;

public class PS implements PSConstants, PSTables, HuffmanTables {

	//bitstream
	private boolean frameClass;
	private int envCount;
	private int[] borderPosition;
	private boolean iidEnabled, iccEnabled, extEnabled, ipdopdEnabled;
	private int iidMode, iccMode, ipdMode;
	private int iidPars, iccPars, ipdopdPars;
	private boolean[] iidTime, iccTime, ipdTime, opdTime;
	//indices
	private int[][] iidIndex, iccIndex, ipdIndex, opdIndex;
	private final int[] iidIndexPrev, iccIndexPrev, ipdIndexPrev, opdIndexPrev;
	private boolean dataAvailable, header;
	//hybrid filterbank
	private final float[][][] bufferLeft, bufferRight; //main processing buffers
	private final Filterbank filterBank;
	private boolean use34;
	private int groups, hybridGroups;
	private int parBands;
	private int decayCutoff;
	private int[] groupBorder;
	private int[] mapGroupToBK;
	//filter delay handling
	private int savedDelay;
	private int[] delayBufIndexSer;
	private final int[] sampleDelaySerCount;
	private final int[] delayD;
	private int[] delayBufIndexDelay;
	private final float[][][] delayQMF, delaySubQMF;
	private final float[][][][] delayQMFSer, delaySubQMFSer;
	//transients
	private float[] peakDecayEnergy;
	private float[] pPrev;
	private float[] smoothPeakDecayDiffEnergyPrev;
	//decorrelate
	final int[][] energy;
	//mixing and phase
	private int phaseHist;
	private float[][] h11Prev, h12Prev, h21Prev, h22Prev;
	private float[][][] ipdPrev, opdPrev;
	private int iidSteps;
	private float[] sfIID;
	private final float[][] h1, h2, H1, H2;
	private final float[] deltaH11, deltaH12, deltaH21, deltaH22;
	private final float[] tempLeft, tempRight, inLeft, inRight;

	public PS() {
		//init arrays
		borderPosition = new int[MAX_PS_ENVELOPES+1];
		iidTime = new boolean[MAX_PS_ENVELOPES];
		iccTime = new boolean[MAX_PS_ENVELOPES];
		ipdTime = new boolean[MAX_PS_ENVELOPES];
		opdTime = new boolean[MAX_PS_ENVELOPES];
		iidIndexPrev = new int[34];
		iccIndexPrev = new int[34];
		ipdIndexPrev = new int[17];
		opdIndexPrev = new int[17];
		iidIndex = new int[MAX_PS_ENVELOPES][34];
		iccIndex = new int[MAX_PS_ENVELOPES][34];
		ipdIndex = new int[MAX_PS_ENVELOPES][17];
		opdIndex = new int[MAX_PS_ENVELOPES][17];
		delayBufIndexSer = new int[NO_ALLPASS_LINKS];
		sampleDelaySerCount = new int[NO_ALLPASS_LINKS];
		delayD = new int[64];
		delayBufIndexDelay = new int[64];
		delayQMF = new float[14][64][2]; //14 samples delay max, 64 QMF channels, complex
		delaySubQMF = new float[2][32][2]; //2 samples delay max, complex
		delayQMFSer = new float[NO_ALLPASS_LINKS][5][64][2]; //5 samples delay max, 64 QMF channels, complex
		delaySubQMFSer = new float[NO_ALLPASS_LINKS][5][32][2]; //5 samples delay max, complex
		peakDecayEnergy = new float[34];
		pPrev = new float[34];
		smoothPeakDecayDiffEnergyPrev = new float[34];
		energy = new int[32][34];
		h11Prev = new float[50][2];
		h12Prev = new float[50][2];
		h21Prev = new float[50][2];
		h22Prev = new float[50][2];
		ipdPrev = new float[20][2][2];
		opdPrev = new float[20][2][2];
		bufferLeft = new float[32][32][2];
		bufferRight = new float[32][32][2];
		h1 = new float[2][2];
		h2 = new float[2][2];
		H1 = new float[2][2];
		H2 = new float[2][2];
		deltaH11 = new float[2];
		deltaH12 = new float[2];
		deltaH21 = new float[2];
		deltaH22 = new float[2];
		tempLeft = new float[2];
		tempRight = new float[2];
		inLeft = new float[2];
		inRight = new float[2];
		//
		filterBank = new Filterbank();
		dataAvailable = false;
		savedDelay = 0;

		int i;
		for(i = 0; i<NO_ALLPASS_LINKS; i++) {
			sampleDelaySerCount[i] = DELAY_LENGTH_D[i];
		}
		for(i = 0; i<SHORT_DELAY_BAND; i++) {
			delayD[i] = 14;
		}
		for(i = SHORT_DELAY_BAND; i<64; i++) {
			delayD[i] = 1;
		}

		//mixing and phase
		for(i = 0; i<50; i++) {
			h11Prev[i][0] = 1;
			h12Prev[i][1] = 1;
			h11Prev[i][0] = 1;
			h12Prev[i][1] = 1;
		}
		phaseHist = 0;
	}

	//============= decoding =============
	public int decode(BitStream in) throws AACException {
		final int bits = in.getPosition();

		//check for new header
		if(in.readBool()) {
			if(!header) header = true;
			dataAvailable = true;
			use34 = false;

			//read flags and modes
			//Inter-channel Intensity Difference (IID)
			if(iidEnabled = in.readBool()) {
				iidMode = in.readBits(3);
				if(iidMode==2||iidMode==5) use34 = true;
				iidPars = NR_IID_PAR_TAB[iidMode];
				ipdopdPars = NR_IPDOPD_PAR_TAB[iidMode];
				ipdMode = iidMode;
			}

			//Inter-channel Coherence (ICC)
			if(iccEnabled = in.readBool()) {
				iccMode = in.readBits(3);
				iccPars = NR_ICC_PAR_TAB[iccMode];
				if(iccMode==2||iccMode==5) use34 = true;
			}

			//extensions
			extEnabled = in.readBool();
		}

		int n;
		frameClass = in.readBool();
		envCount = NUM_ENV_TAB[frameClass ? 1 : 0][in.readBits(2)];
		if(frameClass) {
			for(n = 1; n<envCount+1; n++) {
				borderPosition[n] = in.readBits(5)+1;
			}
		}

		//read huffman data
		if(iidEnabled) {
			for(n = 0; n<envCount; n++) {
				iidTime[n] = in.readBool();
				if(iidMode<3) Huffman.decode(in, iidTime[n], iidPars, T_HUFF_IID_DEF, F_HUFF_IID_DEF, iidIndex[n]);
				else Huffman.decode(in, iidTime[n], iidPars, T_HUFF_IID_FINE, F_HUFF_IID_FINE, iidIndex[n]);
			}
		}

		if(iccEnabled) {
			for(n = 0; n<envCount; n++) {
				iccTime[n] = in.readBool();
				Huffman.decode(in, iccTime[n], iccPars, T_HUFF_ICC, F_HUFF_ICC, iccIndex[n]);
			}
		}

		if(extEnabled) {
			int cnt = in.readBits(4);
			if(cnt==15) cnt += in.readBits(8);

			int bitsLeft = 8*cnt;
			int extensionID;
			while(bitsLeft>7) {
				extensionID = in.readBits(2);
				bitsLeft -= 2;
				bitsLeft -= decodeExtension(in, extensionID);
			}
			in.skipBits(bitsLeft);
		}

		return in.getPosition()-bits;
	}

	private int decodeExtension(BitStream in, int extensionID) throws AACException {
		final int bits = in.getPosition();

		if(extensionID==EXTENSION_ID_IPDOPD) {
			if(ipdopdEnabled = in.readBool()) {
				for(int n = 0; n<envCount; n++) {
					ipdTime[n] = in.readBool();
					Huffman.decode(in, ipdTime[n], ipdopdPars, T_HUFF_IPD, F_HUFF_IPD, ipdIndex[n]);

					opdTime[n] = in.readBool();
					Huffman.decode(in, opdTime[n], ipdopdPars, T_HUFF_OPD, F_HUFF_OPD, opdIndex[n]);
				}
			}
			in.skipBit(); //reserved
		}

		return in.getPosition()-bits;
	}

	public boolean hasHeader() {
		return header;
	}

	//============= processing =============
	public void process(float[][][] left, float[][][] right) {
		//delta decoding of the bitstream data
		parseData();

		//set up parameters depending on filterbank type
		if(use34) {
			groupBorder = GROUP_BORDER34;
			mapGroupToBK = MAP_GROUP2BK34;
			groups = 32+18;
			hybridGroups = 32;
			parBands = 34;
			decayCutoff = 5;
		}
		else {
			groupBorder = GROUP_BORDER20;
			mapGroupToBK = MAP_GROUP2BK20;
			groups = 10+12;
			hybridGroups = 10;
			parBands = 20;
			decayCutoff = 3;
		}

		//perform further analysis on the lowest subbands to get a higher frequency resolution
		filterBank.performAnalysis(left, bufferLeft, use34);

		//decorrelate mono signal
		decorrelate(left, right, bufferLeft, bufferRight);

		//apply mixing and phase parameters
		mixPhase(left, right, bufferLeft, bufferRight);

		//hybrid synthesis, to rebuild the SBR QMF matrices
		filterBank.performSynthesis(bufferLeft, left, use34);
		filterBank.performSynthesis(bufferRight, right, use34);
	}

	//============= parsing =============
	//parses the decoded bitstream data
	private void parseData() {
		//if no data available, use data from previous frame
		if(!dataAvailable) envCount = 0;

		//set iidSteps and sfIID
		if(iidMode>=3) {
			iidSteps = IID_STEPS_LONG;
			sfIID = SF_IID_FINE;
		}
		else {
			iidSteps = IID_STEPS_SHORT;
			sfIID = SF_IID_NORMAL;
		}

		int i, j;
		int[] iid, icc, ipd, opd;
		for(i = 0; i<envCount; i++) {
			if(i==0) {
				iid = iidIndexPrev;
				icc = iccIndexPrev;
				ipd = ipdIndexPrev;
				opd = opdIndexPrev;
			}
			else {
				iid = iidIndex[i-1];
				icc = iccIndex[i-1];
				ipd = ipdIndex[i-1];
				opd = opdIndex[i-1];
			}

			deltaDecode(iidEnabled, iidIndex[i], iid, iidTime[i],
					iidPars, (iidMode==0||iidMode==3) ? 2 : 1, -iidSteps, iidSteps);
			deltaDecode(iccEnabled, iccIndex[i], icc, iccTime[i],
					iccPars, (iccMode==0||iccMode==3) ? 2 : 1, 0, 7);
			deltaModuloDecode(ipdopdEnabled, ipdIndex[i], ipd,
					ipdTime[i], ipdopdPars, 1, 7);
			deltaModuloDecode(ipdopdEnabled, opdIndex[i], opd,
					opdTime[i], ipdopdPars, 1, 7);
		}

		if(envCount==0) {
			//error case
			envCount = 1;

			if(iidEnabled) {
				for(j = 0; j<34; j++) {
					iidIndex[0][j] = iidIndexPrev[j];
				}
			}
			else {
				for(j = 0; j<34; j++) {
					iidIndex[0][j] = 0;
				}
			}

			if(iccEnabled) {
				for(j = 0; j<34; j++) {
					iccIndex[0][j] = iccIndexPrev[j];
				}
			}
			else {
				for(j = 0; j<34; j++) {
					iccIndex[0][j] = 0;
				}
			}

			if(ipdopdEnabled) {
				for(j = 0; j<17; j++) {
					ipdIndex[0][j] = ipdIndexPrev[j];
					opdIndex[0][j] = opdIndexPrev[j];
				}
			}
			else {
				for(j = 0; j<17; j++) {
					ipdIndex[0][j] = 0;
					opdIndex[0][j] = 0;
				}
			}
		}

		//update previous indices
		System.arraycopy(iidIndex[envCount-1], 0, iidIndexPrev, 0, 34);
		System.arraycopy(iccIndex[envCount-1], 0, iccIndexPrev, 0, 34);
		System.arraycopy(ipdIndex[envCount-1], 0, ipdIndexPrev, 0, 17);
		System.arraycopy(opdIndex[envCount-1], 0, opdIndexPrev, 0, 17);

		dataAvailable = false;

		borderPosition[0] = 0;
		if(frameClass) {
			if(borderPosition[envCount]<TIME_SLOTS_RATE) {
				System.arraycopy(iidIndex[envCount-1], 0, iidIndex[envCount], 0, 34);
				System.arraycopy(iccIndex[envCount-1], 0, iccIndex[envCount], 0, 17);
				envCount++;
				borderPosition[envCount] = TIME_SLOTS_RATE;
			}

			int thr;
			for(i = 1; i<envCount; i++) {
				thr = TIME_SLOTS_RATE-(envCount-i);

				if(borderPosition[i]>thr) borderPosition[i] = thr;
				else {
					thr = borderPosition[i-1]+1;
					if(borderPosition[i]<thr) borderPosition[i] = thr;
				}
			}
		}
		else {
			for(i = 1; i<envCount; i++) {
				borderPosition[i] = (i*TIME_SLOTS_RATE)/envCount;
			}
			borderPosition[envCount] = TIME_SLOTS_RATE;
		}

		/* make sure that the indices of all parameters can be mapped
		 * to the same hybrid synthesis filterbank */
		if(use34) {
			for(i = 0; i<envCount; i++) {
				if(iidMode!=2&&iidMode!=5) map20IndexTo34(iidIndex[i], 34);
				if(iccMode!=2&&iccMode!=5) map20IndexTo34(iccIndex[i], 34);
				if(ipdMode!=2&&ipdMode!=5) {
					map20IndexTo34(ipdIndex[i], 17);
					map20IndexTo34(opdIndex[i], 17);
				}
			}
		}
	}

	private void deltaDecode(boolean enabled, int[] index, int[] indexPrev,
			boolean time, int parCount, int stride, int minIndex, int maxIndex) {
		int i;

		if(enabled) {
			if(time) {
				for(i = 0; i<parCount; i++) {
					index[i] = indexPrev[i*stride]+index[i];
					index[i] = Math.min(maxIndex, Math.max(index[i], minIndex));
				}
			}
			else {
				index[0] = Math.min(maxIndex, Math.max(index[0], minIndex));
				for(i = 1; i<parCount; i++) {
					index[i] = index[i-1]+index[i];
					index[i] = Math.min(maxIndex, Math.max(index[i], minIndex));
				}

			}
		}
		else {
			for(i = 0; i<parCount; i++) {
				index[i] = 0;
			}
		}

		//coarse
		if(stride==2) {
			for(i = (parCount<<1)-1; i>0; i--) {
				index[i] = index[i>>1];
			}
		}
	}

	private void deltaModuloDecode(boolean enabled, int[] index, int[] indexPrev,
			boolean time, int parCount, int stride, int andModulo) {
		int i;

		if(enabled) {
			if(time) {
				for(i = 0; i<parCount; i++) {
					index[i] = indexPrev[i*stride]+index[i];
					index[i] &= andModulo;
				}
			}
			else {
				index[0] &= andModulo;
				for(i = 1; i<parCount; i++) {
					index[i] = index[i-1]+index[i];
					index[i] &= andModulo;
				}
			}
		}
		else {
			for(i = 0; i<parCount; i++) {
				index[i] = 0;
			}
		}

		//coarse
		if(stride==2) {
			index[0] = 0;
			for(i = (parCount<<1)-1; i>0; i--) {
				index[i] = index[i>>1];
			}
		}
	}

	private void map20IndexTo34(int[] index, int bins) {
		index[0] = index[0];
		index[1] = (index[0]+index[1])>>>1;
		index[2] = index[1];
		index[3] = index[2];
		index[4] = (index[2]+index[3])>>>1;
		index[5] = index[3];
		index[6] = index[4];
		index[7] = index[4];
		index[8] = index[5];
		index[9] = index[5];
		index[10] = index[6];
		index[11] = index[7];
		index[12] = index[8];
		index[13] = index[8];
		index[14] = index[9];
		index[15] = index[9];
		index[16] = index[10];

		if(bins==34) {
			index[17] = index[11];
			index[18] = index[12];
			index[19] = index[13];
			index[20] = index[14];
			index[21] = index[14];
			index[22] = index[15];
			index[23] = index[15];
			index[24] = index[16];
			index[25] = index[16];
			index[26] = index[17];
			index[27] = index[17];
			index[28] = index[18];
			index[29] = index[18];
			index[30] = index[18];
			index[31] = index[18];
			index[32] = index[19];
			index[33] = index[19];
		}
	}

	//============= decorrelation =============
	//decorrelates the mono signal using an allpass filter
	private void decorrelate(float[][][] leftQMF, float[][][] rightQMF, float[][][] leftHybrid, float[][][] rightHybrid) {
		//chose hybrid filterbank: 20 or 34 band case
		final float[][] phiFractSubQMF = use34 ? PHI_FRACT_SUBQMF34 : PHI_FRACT_SUBQMF20;
		final float[][][] qFractAllpassSubQMF = use34 ? Q_FRACT_ALLPASS_SUBQMF34 : Q_FRACT_ALLPASS_SUBQMF20;

		//step 1: calculate the energy in each parameter band
		calculateEnergy(leftQMF, leftHybrid);

		//step 2: calculate transient reduction ratio for each parameter band
		final float[][] gTransientRatio = calculateReductionRatio(energy);

		//step 3: apply stereo decorrelation filter to the signal
		float gDecaySlope;
		float[] gDecaySlopeFilt = new float[NO_ALLPASS_LINKS];
		float[] tmp0 = new float[2], tmp1 = new float[2], tmp2 = new float[2];
		float[] saved = new float[2];
		float[] phiFract = new float[2];
		float[] qFractAllpass = new float[2];
		float[][] phiFractX;
		float[][][] storeX, delayX, qFractAllpassX, inputX;
		float[][][][] delaySerX;
		int tempDelay = 0;
		int[] tempDelaySer = new int[NO_ALLPASS_LINKS];
		int sb, maxSB, bk, n, m, decay;
		for(int gr = 0; gr<groups; gr++) {
			bk = (~NEGATE_IPD_MASK)&mapGroupToBK[gr];

			if(gr<hybridGroups) {
				maxSB = groupBorder[gr]+1;
				storeX = rightHybrid;
				delaySerX = delaySubQMFSer;
				phiFractX = phiFractSubQMF;
				delayX = delaySubQMF;
				qFractAllpassX = qFractAllpassSubQMF;
				inputX = leftHybrid;
			}
			else {
				maxSB = groupBorder[gr+1];
				storeX = rightQMF;
				delaySerX = delayQMFSer;
				phiFractX = PHI_FRACT_QMF;
				delayX = delayQMF;
				qFractAllpassX = Q_FRACT_ALLPASS_QMF;
				inputX = leftQMF;
			}

			for(sb = groupBorder[gr]; sb<maxSB; sb++) {
				if(gr<hybridGroups||sb<=decayCutoff) gDecaySlope = 1.0f;
				else {
					decay = decayCutoff-sb;
					if(decay<=-20) gDecaySlope = 0;
					else gDecaySlope = 1.0f+DECAY_SLOPE*decay;
				}

				//calculate gDecaySlopeFilt for every m multiplied by FILTER_A[m]
				for(m = 0; m<NO_ALLPASS_LINKS; m++) {
					gDecaySlopeFilt[m] = gDecaySlope*FILTER_A[m];
				}

				//set delay indices
				tempDelay = savedDelay;
				for(n = 0; n<NO_ALLPASS_LINKS; n++) {
					tempDelaySer[n] = delayBufIndexSer[n];
				}

				for(n = borderPosition[0]; n<borderPosition[envCount]; n++) {
					if(sb>NR_ALLPASS_BANDS&&gr>=hybridGroups) {
						tmp0[0] = delayQMF[delayBufIndexDelay[sb]][sb][0];
						tmp0[1] = delayQMF[delayBufIndexDelay[sb]][sb][1];
						saved[0] = tmp0[0];
						saved[1] = tmp0[1];
						delayQMF[delayBufIndexDelay[sb]][sb][0] = inputX[n][sb][0];
						delayQMF[delayBufIndexDelay[sb]][sb][1] = inputX[n][sb][1];
					}
					else {
						//select data from the subbands
						tmp1[0] = delayX[tempDelay][sb][0];
						tmp1[1] = delayX[tempDelay][sb][1];
						delayX[tempDelay][sb][0] = inputX[n][sb][0];
						delayX[tempDelay][sb][1] = inputX[n][sb][1];
						phiFract[0] = phiFractX[sb][0];
						phiFract[1] = phiFractX[sb][1];

						//z^(-2) * Phi_Fract[k]
						tmp0[0] = (tmp1[0]*phiFract[0])+(tmp1[1]*phiFract[1]);
						tmp0[1] = (tmp1[1]*phiFract[0])-(tmp1[0]*phiFract[1]);

						saved[0] = tmp0[0];
						saved[1] = tmp0[1];
						for(m = 0; m<NO_ALLPASS_LINKS; m++) {
							//select data from the subbands
							tmp1[0] = delaySerX[m][tempDelaySer[m]][sb][0];
							tmp1[1] = delaySerX[m][tempDelaySer[m]][sb][1];
							qFractAllpass[0] = qFractAllpassX[sb][m][0];
							qFractAllpass[1] = qFractAllpassX[sb][m][1];

							//delay by a fraction:  z^(-d(m)) * qFractAllpass[k,m]
							tmp0[0] = (tmp1[0]*qFractAllpass[0])+(tmp1[1]*qFractAllpass[1]);
							tmp0[1] = (tmp1[1]*qFractAllpass[0])-(tmp1[0]*qFractAllpass[1]);

							//-a(m) * gDecaySlope[k]
							tmp0[0] += -(gDecaySlopeFilt[m]*saved[0]);
							tmp0[1] += -(gDecaySlopeFilt[m]*saved[1]);

							//-a(m) * gDecaySlope[k] * qFractAllpass[k,m] * z^(-d(m))
							tmp2[0] = saved[0]+(gDecaySlopeFilt[m]*tmp0[0]);
							tmp2[1] = saved[1]+(gDecaySlopeFilt[m]*tmp0[1]);

							//store sample to delaySubQMFSer or delayQMFSer
							delaySerX[m][tempDelaySer[m]][sb][0] = tmp2[0];
							delaySerX[m][tempDelaySer[m]][sb][1] = tmp2[1];

							//store for next iteration (or as output value if last iteration)
							saved[0] = tmp0[0];
							saved[1] = tmp0[1];
						}
					}

					//duck if a past transient is found
					saved[0] *= gTransientRatio[n][bk];
					saved[1] *= gTransientRatio[n][bk];

					//store to rightHybrid or rightQMF
					storeX[n][sb][0] = saved[0];
					storeX[n][sb][1] = saved[1];

					//update delay buffer index
					tempDelay++;
					if(tempDelay>=2) tempDelay = 0;

					//update delay indices
					if(sb>NR_ALLPASS_BANDS&&gr>=hybridGroups) {
						delayBufIndexDelay[sb]++;
						if(delayBufIndexDelay[sb]>=delayD[sb]) delayBufIndexDelay[sb] = 0;
					}

					for(m = 0; m<NO_ALLPASS_LINKS; m++) {
						tempDelaySer[m]++;
						if(tempDelaySer[m]>=sampleDelaySerCount[m]) tempDelaySer[m] = 0;
					}
				}
			}
		}

		//update indices
		savedDelay = tempDelay;
		for(m = 0; m<NO_ALLPASS_LINKS; m++) {
			delayBufIndexSer[m] = tempDelaySer[m];
		}
	}

	//fills the 'energy' array
	private void calculateEnergy(float[][][] leftQMF, float[][][] leftHybrid) {
		final float[] tmp = new float[2];

		float[][][] in;
		int bk, sb, maxSB, n;
		for(int gr = 0; gr<groups; gr++) {
			//select the parameter index b(k) to which this group belongs
			bk = (~NEGATE_IPD_MASK)&mapGroupToBK[gr];

			//select the upper subband border for this group
			if(gr<hybridGroups) {
				maxSB = groupBorder[gr]+1;
				in = leftHybrid;
			}
			else {
				maxSB = groupBorder[gr+1];
				in = leftQMF;
			}

			for(sb = groupBorder[gr]; sb<maxSB; sb++) {
				for(n = borderPosition[0]; n<borderPosition[envCount]; n++) {
					//input from hybrid subbands or QMF subbands
					tmp[0] = in[n][sb][0];
					tmp[1] = in[n][sb][1];
					//accumulate energy
					energy[n][bk] += (tmp[0]*tmp[0])+(tmp[1]*tmp[1]);
				}
			}
		}
	}

	private float[][] calculateReductionRatio(int[][] p) {
		final float[][] out = new float[32][34];

		float smoothPeakDecayDiffEnergy, en;
		int n;
		for(int bk = 0; bk<parBands; bk++) {
			for(n = borderPosition[0]; n<borderPosition[envCount]; n++) {
				peakDecayEnergy[bk] = (peakDecayEnergy[bk]*ALPHA_DECAY);
				if(peakDecayEnergy[bk]<p[n][bk]) peakDecayEnergy[bk] = p[n][bk];

				//apply smoothing filter to peak decay energy
				smoothPeakDecayDiffEnergy = smoothPeakDecayDiffEnergyPrev[bk];
				smoothPeakDecayDiffEnergy += ((peakDecayEnergy[bk]-p[n][bk]-smoothPeakDecayDiffEnergyPrev[bk])*ALPHA_SMOOTH);
				smoothPeakDecayDiffEnergyPrev[bk] = smoothPeakDecayDiffEnergy;

				//apply smoothing filter to energy
				en = pPrev[bk];
				en += (p[n][bk]-pPrev[bk])*ALPHA_SMOOTH;
				pPrev[bk] = en;

				//calculate transient ratio
				if((smoothPeakDecayDiffEnergy*REDUCTION_RATIO_GAMMA)<=en) out[n][bk] = 1.0f;
				else out[n][bk] = en/(smoothPeakDecayDiffEnergy*REDUCTION_RATIO_GAMMA);
			}
		}

		return out;
	}

	//============= mixing/phase =============
	private void mixPhase(float[][][] leftQMF, float[][][] rightQMF, float[][][] leftHybrid, float[][][] rightHybrid) {
		final int curIpdopdPars = (ipdMode==0||ipdMode==3) ? 11 : ipdopdPars;

		int n, sb, maxsb, env, bk;
		float L;
		for(int gr = 0; gr<groups; gr++) {
			bk = (~NEGATE_IPD_MASK)&mapGroupToBK[gr];

			//use one channel per group in the subqmf domain
			maxsb = (gr<hybridGroups) ? groupBorder[gr]+1 : groupBorder[gr+1];

			for(env = 0; env<envCount; env++) {
				//mixing
				if(iccMode<3) applyMixingA(env, bk, h1, h2);
				else applyMixingB(env, bk, h1, h2);

				//calculate phase rotation parameters
				if((ipdopdEnabled)&&(bk<curIpdopdPars)) calculatePhaseRotation(env, bk, h1, h2);

				//length of the envelope (in time samples); 0 < L <= 32
				L = (float) (borderPosition[env+1]-borderPosition[env]);

				//obtain final H by means of linear interpolation
				deltaH11[0] = (h1[0][0]-h11Prev[gr][0])/L;
				deltaH12[0] = (h1[1][0]-h12Prev[gr][0])/L;
				deltaH21[0] = (h2[0][0]-h21Prev[gr][0])/L;
				deltaH22[0] = (h2[1][0]-h22Prev[gr][0])/L;

				H1[0][0] = h11Prev[gr][0];
				H1[1][0] = h12Prev[gr][0];
				H2[0][0] = h21Prev[gr][0];
				H2[1][0] = h22Prev[gr][0];

				h11Prev[gr][0] = h1[0][0];
				h12Prev[gr][0] = h1[1][0];
				h21Prev[gr][0] = h2[0][0];
				h22Prev[gr][0] = h2[1][0];

				if((ipdopdEnabled)&&(bk<curIpdopdPars)) {
					//obtain final H_xy by means of linear interpolation
					deltaH11[1] = (h1[0][1]-h11Prev[gr][1])/L;
					deltaH12[1] = (h1[1][1]-h12Prev[gr][1])/L;
					deltaH21[1] = (h2[0][1]-h21Prev[gr][1])/L;
					deltaH22[1] = (h2[1][1]-h22Prev[gr][1])/L;

					H1[0][1] = h11Prev[gr][1];
					H1[1][1] = h12Prev[gr][1];
					H2[0][1] = h21Prev[gr][1];
					H2[1][1] = h22Prev[gr][1];

					if((NEGATE_IPD_MASK&mapGroupToBK[gr])!=0) {
						deltaH11[1] = -deltaH11[1];
						deltaH12[1] = -deltaH12[1];
						deltaH21[1] = -deltaH21[1];
						deltaH22[1] = -deltaH22[1];

						H1[0][1] = -H1[0][1];
						H1[1][1] = -H1[1][1];
						H2[0][1] = -H2[0][1];
						H2[1][1] = -H2[1][1];
					}

					h11Prev[gr][1] = h1[0][1];
					h12Prev[gr][1] = h1[1][1];
					h21Prev[gr][1] = h2[0][1];
					h22Prev[gr][1] = h2[1][1];
				}

				//apply H_xy to the current envelope band of the decorrelated subband
				for(n = borderPosition[env]; n<borderPosition[env+1]; n++) {
					//addition finalises the interpolation over every n
					H1[0][0] += deltaH11[0];
					H1[1][0] += deltaH12[0];
					H2[0][0] += deltaH21[0];
					H2[1][0] += deltaH22[0];
					if((ipdopdEnabled)&&(bk<curIpdopdPars)) {
						H1[0][1] += deltaH11[1];
						H1[1][1] += deltaH12[1];
						H2[0][1] += deltaH21[1];
						H2[1][1] += deltaH22[1];
					}

					//channel is an alias to the subband
					for(sb = groupBorder[gr]; sb<maxsb; sb++) {
						/* load decorrelated samples */
						if(gr<hybridGroups) {
							inLeft[0] = leftHybrid[n][sb][0];
							inLeft[1] = leftHybrid[n][sb][1];
							inRight[0] = rightHybrid[n][sb][0];
							inRight[1] = rightHybrid[n][sb][1];
						}
						else {
							inLeft[0] = leftQMF[n][sb][0];
							inLeft[1] = leftQMF[n][sb][1];
							inRight[0] = rightQMF[n][sb][0];
							inRight[1] = rightQMF[n][sb][1];
						}

						//apply mixing
						tempLeft[0] = (H1[0][0]*inLeft[0])+(H2[0][0]*inRight[0]);
						tempLeft[1] = (H1[0][0]*inLeft[1])+(H2[0][0]*inRight[1]);
						tempRight[0] = (H1[1][0]*inLeft[0])+(H2[1][0]*inRight[0]);
						tempRight[1] = (H1[1][0]*inLeft[1])+(H2[1][0]*inRight[1]);

						if((ipdopdEnabled)&&(bk<curIpdopdPars)) {
							//apply rotation
							tempLeft[0] -= (H1[0][1]*inLeft[1])+(H2[0][1]*inRight[1]);
							tempLeft[1] += (H1[0][1]*inLeft[0])+(H2[0][1]*inRight[0]);
							tempRight[0] -= (H1[1][1]*inLeft[1])+(H2[1][1]*inRight[1]);
							tempRight[1] += (H1[1][1]*inLeft[0])+(H2[1][1]*inRight[0]);
						}

						//store final samples
						if(gr<hybridGroups) {
							leftHybrid[n][sb][0] = tempLeft[0];
							leftHybrid[n][sb][1] = tempLeft[1];
							rightHybrid[n][sb][0] = tempRight[0];
							rightHybrid[n][sb][1] = tempRight[1];
						}
						else {
							leftQMF[n][sb][0] = tempLeft[0];
							leftQMF[n][sb][1] = tempLeft[1];
							rightQMF[n][sb][0] = tempRight[0];
							rightQMF[n][sb][1] = tempRight[1];
						}
					}
				}

				//shift phase smoother's circular buffer index
				phaseHist++;
				if(phaseHist==2) phaseHist = 0;
			}
		}
	}

	//type 'A' mixing
	private void applyMixingA(int env, int bk, float[][] out1, float[][] out2) {
		//calculate the scalefactors c1 and c2 from the intensity differences
		final float c1 = sfIID[iidSteps+iidIndex[env][bk]];
		final float c2 = sfIID[iidSteps-iidIndex[env][bk]];

		//calculate alpha and beta using the ICC parameters
		final float cosa = COS_ALPHAS[iccIndex[env][bk]];
		final float sina = SIN_ALPHAS[iccIndex[env][bk]];

		float cosb, sinb;
		if(iidMode>=3) {
			if(iidIndex[env][bk]<0) {
				cosb = COS_BETAS_FINE[-iidIndex[env][bk]][iccIndex[env][bk]];
				sinb = -SIN_BETAS_FINE[-iidIndex[env][bk]][iccIndex[env][bk]];
			}
			else {
				cosb = COS_BETAS_FINE[iidIndex[env][bk]][iccIndex[env][bk]];
				sinb = SIN_BETAS_FINE[iidIndex[env][bk]][iccIndex[env][bk]];
			}
		}
		else {
			if(iidIndex[env][bk]<0) {
				cosb = COS_BETAS_NORMAL[-iidIndex[env][bk]][iccIndex[env][bk]];
				sinb = -SIN_BETAS_NORMAL[-iidIndex[env][bk]][iccIndex[env][bk]];
			}
			else {
				cosb = COS_BETAS_NORMAL[iidIndex[env][bk]][iccIndex[env][bk]];
				sinb = SIN_BETAS_NORMAL[iidIndex[env][bk]][iccIndex[env][bk]];
			}
		}

		final float ab1 = cosb*cosa;
		final float ab2 = sinb*sina;
		final float ab3 = sinb*cosa;
		final float ab4 = cosb*sina;

		out1[0][0] = c2*(ab1-ab2);
		out1[1][0] = c1*(ab1+ab2);
		out2[0][0] = c2*(ab3+ab4);
		out2[1][0] = c1*(ab3-ab4);
	}

	//type 'B' mixing
	private void applyMixingB(int env, int bk, float[][] out1, float[][] out2) {
		float cosa, sina, cosg, sing;
		final int absIID = Math.abs(iidIndex[env][bk]);
		if(iidMode>=3) {
			cosa = SINCOS_ALPHAS_B_FINE[iidSteps+iidIndex[env][bk]][iccIndex[env][bk]];
			sina = SINCOS_ALPHAS_B_FINE[30-(iidSteps+iidIndex[env][bk])][iccIndex[env][bk]];
			cosg = COS_GAMMAS_FINE[absIID][iccIndex[env][bk]];
			sing = SIN_GAMMAS_FINE[absIID][iccIndex[env][bk]];
		}
		else {
			cosa = SINCOS_ALPHAS_B_NORMAL[iidSteps+iidIndex[env][bk]][iccIndex[env][bk]];
			sina = SINCOS_ALPHAS_B_NORMAL[14-(iidSteps+iidIndex[env][bk])][iccIndex[env][bk]];
			cosg = COS_GAMMAS_NORMAL[absIID][iccIndex[env][bk]];
			sing = SIN_GAMMAS_NORMAL[absIID][iccIndex[env][bk]];
		}

		out1[0][0] = SQRT2*(cosa*cosg);
		out1[1][0] = SQRT2*(sina*cosg);
		out2[0][0] = SQRT2*(-cosa*sing);
		out2[1][0] = SQRT2*(sina*sing);
	}

	private void calculatePhaseRotation(int env, int bk, float[][] out1, float[][] out2) {
		final float[] tempLeft = new float[2];
		final float[] tempRight = new float[2];
		final float[] phaseLeft = new float[2];
		final float[] phaseRight = new float[2];
		int i = phaseHist; //ringbuffer index

		//previous value
		tempLeft[0] = ipdPrev[bk][i][0]*0.25f;
		tempLeft[1] = ipdPrev[bk][i][1]*0.25f;
		tempRight[0] = opdPrev[bk][i][0]*0.25f;
		tempRight[1] = opdPrev[bk][i][1]*0.25f;

		//save current value
		ipdPrev[bk][i][0] = IPDOPD_COS_TAB[Math.abs(ipdIndex[env][bk])];
		ipdPrev[bk][i][1] = IPDOPD_SIN_TAB[Math.abs(ipdIndex[env][bk])];
		opdPrev[bk][i][0] = IPDOPD_COS_TAB[Math.abs(opdIndex[env][bk])];
		opdPrev[bk][i][1] = IPDOPD_SIN_TAB[Math.abs(opdIndex[env][bk])];

		//add current value
		tempLeft[0] += ipdPrev[bk][i][0];
		tempLeft[1] += ipdPrev[bk][i][1];
		tempRight[0] += opdPrev[bk][i][0];
		tempRight[1] += opdPrev[bk][i][1];

		if(i==0) i = 2;
		i--;

		//get value before previous
		tempLeft[0] += ipdPrev[bk][i][0]*0.5f;
		tempLeft[1] += ipdPrev[bk][i][1]*0.5f;
		tempRight[0] += opdPrev[bk][i][0]*0.5f;
		tempRight[1] += opdPrev[bk][i][1]*0.5f;

		final float xy = (float) Math.sqrt(tempRight[0]*tempRight[0]+tempRight[1]*tempRight[1]);
		final float pq = (float) Math.sqrt(tempLeft[0]*tempLeft[0]+tempLeft[1]*tempLeft[1]);

		if(xy==0) {
			phaseLeft[0] = 0;
			phaseLeft[1] = 0;
		}
		else {
			phaseLeft[0] = tempRight[0]/xy;
			phaseLeft[1] = tempRight[1]/xy;
		}

		final float xypq = xy*pq;
		if(xypq==0) {
			phaseRight[0] = 0;
			phaseRight[1] = 0;
		}
		else {
			final float tmp1 = (tempRight[0]*tempLeft[0])+(tempRight[1]*tempLeft[1]);
			final float tmp2 = (tempRight[1]*tempLeft[0])-(tempRight[0]*tempLeft[1]);
			phaseRight[0] = tmp1/xypq;
			phaseRight[1] = tmp2/xypq;
		}

		out1[0][1] = out1[0][0]*phaseLeft[1];
		out1[1][1] = out1[1][0]*phaseRight[1];
		out2[0][1] = out2[0][0]*phaseLeft[1];
		out2[1][1] = out2[1][0]*phaseRight[1];
		out1[0][0] = out1[0][0]*phaseLeft[0];
		out1[1][0] = out1[1][0]*phaseRight[0];
		out2[0][0] = out2[0][0]*phaseLeft[0];
		out2[1][0] = out2[1][0]*phaseRight[0];
	}
}
