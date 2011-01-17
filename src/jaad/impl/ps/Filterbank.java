package jaad.impl.ps;

class Filterbank implements PSConstants, FilterbankTables {

	private final float[][] work;
	private final float[][][] buffer, temp;
	private final float[][] bufa;
	private final float[][] buf12a, buf12b; //extra buffer for channelFilter12
	private final float[] buf8; //extra buffer for channelFilter8
	private final float[] dctBuf; //shared buffer for DCT3-4 and DCT3-6

	Filterbank() {
		work = new float[TIME_SLOTS_RATE+12][2];
		buffer = new float[5][TIME_SLOTS_RATE][2];
		temp = new float[TIME_SLOTS_RATE][12][2];
		bufa = new float[7][7];
		buf8 = new float[4];
		buf12a = new float[6][2];
		buf12b = new float[6][2];
		dctBuf = new float[7];
	}

	public void performAnalysis(float[][][] in, float[][][] out, boolean use34) {
		final int[] resolution = (use34) ? RESOLUTION34 : RESOLUTION20;
		int offset = 0;

		int i, j;
		for(int band = 0; band<resolution.length; band++) {
			//build working buffer (complex copy)
			for(i = 0; i<12; i++) {
				work[i][0] = buffer[band][i][0];
				work[i][1] = buffer[band][i][1];
			}

			//add new samples
			for(i = 0; i<TIME_SLOTS_RATE; i++) {
				work[12+i][0] = in[i+ANALYSIS_DELAY][band][0];
				work[12+i][1] = in[i+ANALYSIS_DELAY][band][1];
			}

			//store samples (complex copy)
			for(i = 0; i<12; i++) {
				buffer[band][i][0] = work[TIME_SLOTS_RATE+i][0];
				buffer[band][i][1] = work[TIME_SLOTS_RATE+i][1];
			}

			switch(resolution[band]) {
				case 2:
					performChannelFilter2(P2_13_20);
					break;
				case 4:
					performChannelFilter4(P4_13_34);
					break;
				case 8:
					performChannelFilter8((use34) ? P8_13_34 : P8_13_20);
					break;
				case 12:
					performChannelFilter12(P12_13_34);
					break;
			}

			for(i = 0; i<TIME_SLOTS_RATE; i++) {
				for(j = 0; j<resolution[band]; j++) {
					out[i][offset+j][0] = temp[i][j][0];
					out[i][offset+j][1] = temp[i][j][1];
				}
			}
			offset += resolution[band];
		}

		//group hybrid channels
		if(!use34) {
			for(i = 0; i<TIME_SLOTS_RATE; i++) {
				out[i][3][0] += out[i][4][0];
				out[i][3][1] += out[i][4][1];
				out[i][4][0] = 0;
				out[i][4][1] = 0;

				out[i][2][0] += out[i][5][0];
				out[i][2][1] += out[i][5][1];
				out[i][5][0] = 0;
				out[i][5][1] = 0;
			}
		}
	}

	public void performSynthesis(float[][][] in, float[][][] out, boolean use34) {
		final int[] resolution = (use34) ? RESOLUTION34 : RESOLUTION20;
		int offset = 0;

		int i, j;
		for(int band = 0; band<resolution.length; band++) {
			for(i = 0; i<TIME_SLOTS_RATE; i++) {
				out[i][band][0] = 0;
				out[i][band][1] = 0;

				for(j = 0; j<resolution[band]; j++) {
					out[i][band][0] += in[i][offset+j][0];
					out[i][band][1] += in[i][offset+j][1];
				}
			}
			offset += resolution[band];
		}
	}

	//real filter, size 2
	private void performChannelFilter2(float[] filter) {
		for(int i = 0; i<TIME_SLOTS_RATE; i++) {
			bufa[0][0] = filter[0]*(work[0+i][0]+work[12+i][0]);
			bufa[0][1] = filter[1]*(work[1+i][0]+work[11+i][0]);
			bufa[0][2] = filter[2]*(work[2+i][0]+work[10+i][0]);
			bufa[0][3] = filter[3]*(work[3+i][0]+work[9+i][0]);
			bufa[0][4] = filter[4]*(work[4+i][0]+work[8+i][0]);
			bufa[0][5] = filter[5]*(work[5+i][0]+work[7+i][0]);
			bufa[0][6] = filter[6]*work[6+i][0];
			bufa[1][0] = filter[0]*(work[0+i][1]+work[12+i][1]);
			bufa[1][1] = filter[1]*(work[1+i][1]+work[11+i][1]);
			bufa[1][2] = filter[2]*(work[2+i][1]+work[10+i][1]);
			bufa[1][3] = filter[3]*(work[3+i][1]+work[9+i][1]);
			bufa[1][4] = filter[4]*(work[4+i][1]+work[8+i][1]);
			bufa[1][5] = filter[5]*(work[5+i][1]+work[7+i][1]);
			bufa[1][6] = filter[6]*work[6+i][1];

			temp[i][0][0] = bufa[0][0]+bufa[0][1]+bufa[0][2]+bufa[0][3]+bufa[0][4]+bufa[0][5]+bufa[0][6];
			temp[i][0][1] = bufa[1][0]+bufa[1][1]+bufa[1][2]+bufa[1][3]+bufa[1][4]+bufa[1][5]+bufa[1][6];

			temp[i][1][0] = bufa[0][0]-bufa[0][1]+bufa[0][2]-bufa[0][3]+bufa[0][4]-bufa[0][5]+bufa[0][6];
			temp[i][1][1] = bufa[1][0]-bufa[1][1]+bufa[1][2]-bufa[1][3]+bufa[1][4]-bufa[1][5]+bufa[1][6];
		}
	}

	//complex filter, size 4
	private void performChannelFilter4(float[] filter) {
		for(int i = 0; i<TIME_SLOTS_RATE; i++) {
			bufa[0][0] = -(filter[2]*(work[i+2][0]+work[i+10][0]))
					+(filter[6]*work[i+6][0]);
			bufa[0][1] = -0.70710678118655f
					*((filter[1]*(work[i+1][0]+work[i+11][0]))
					+(filter[3]*(work[i+3][0]+work[i+9][0]))
					-(filter[5]*(work[i+5][0]+work[i+7][0])));

			bufa[1][0] = (filter[0]*(work[i+0][1]-work[i+12][1]))
					-(filter[4]*(work[i+4][1]-work[i+8][1]));
			bufa[1][1] = 0.70710678118655f
					*((filter[1]*(work[i+1][1]-work[i+11][1]))
					-(filter[3]*(work[i+3][1]-work[i+9][1]))
					-(filter[5]*(work[i+5][1]-work[i+7][1])));

			bufa[2][0] = (filter[0]*(work[i+0][0]-work[i+12][0]))
					-(filter[4]*(work[i+4][0]-work[i+8][0]));
			bufa[2][1] = 0.70710678118655f
					*((filter[1]*(work[i+1][0]-work[i+11][0]))
					-(filter[3]*(work[i+3][0]-work[i+9][0]))
					-(filter[5]*(work[i+5][0]-work[i+7][0])));

			bufa[3][0] = -(filter[2]*(work[i+2][1]+work[i+10][1]))
					+(filter[6]*work[i+6][1]);
			bufa[3][1] = -0.70710678118655f
					*((filter[1]*(work[i+1][1]+work[i+11][1]))
					+(filter[3]*(work[i+3][1]+work[i+9][1]))
					-(filter[5]*(work[i+5][1]+work[i+7][1])));

			temp[i][0][0] = bufa[0][0]+bufa[0][1]+bufa[1][0]+bufa[1][1];
			temp[i][0][1] = -bufa[2][0]-bufa[2][1]+bufa[3][0]+bufa[3][1];

			temp[i][1][0] = bufa[0][0]-bufa[0][1]-bufa[1][0]+bufa[1][1];
			temp[i][1][1] = bufa[2][0]-bufa[2][1]+bufa[3][0]-bufa[3][1];

			temp[i][2][0] = bufa[0][0]-bufa[0][1]+bufa[1][0]-bufa[1][1];
			temp[i][2][1] = -bufa[2][0]+bufa[2][1]+bufa[3][0]-bufa[3][1];

			temp[i][3][0] = bufa[0][0]+bufa[0][1]-bufa[1][0]-bufa[1][1];
			temp[i][3][1] = bufa[2][0]+bufa[2][1]+bufa[3][0]+bufa[3][1];
		}
	}

	//complex filter, size 8
	private void performChannelFilter8(float[] filter) {
		int n;
		for(int i = 0; i<TIME_SLOTS_RATE; i++) {
			bufa[0][0] = filter[6]*work[6+i][0];
			bufa[0][1] = (filter[5]*(work[5+i][0]+work[7+i][0]));
			bufa[0][2] = -(filter[0]*(work[0+i][0]+work[12+i][0]))+(filter[4]*(work[4+i][0]+work[8+i][0]));
			bufa[0][3] = -(filter[1]*(work[1+i][0]+work[11+i][0]))+(filter[3]*(work[3+i][0]+work[9+i][0]));

			bufa[1][0] = (filter[5]*(work[7+i][1]-work[5+i][1]));
			bufa[1][1] = (filter[0]*(work[12+i][1]-work[0+i][1]))+(filter[4]*(work[8+i][1]-work[4+i][1]));
			bufa[1][2] = (filter[1]*(work[11+i][1]-work[1+i][1]))+(filter[3]*(work[9+i][1]-work[3+i][1]));
			bufa[1][3] = (filter[2]*(work[10+i][1]-work[2+i][1]));

			for(n = 0; n<4; n++) {
				buf8[n] = bufa[0][n]-bufa[1][3-n];
			}
			computeDCT3L4(buf8);
			temp[i][7][0] = buf8[0];
			temp[i][5][0] = buf8[2];
			temp[i][3][0] = buf8[3];
			temp[i][1][0] = buf8[1];

			for(n = 0; n<4; n++) {
				buf8[n] = bufa[0][n]+bufa[1][3-n];
			}
			computeDCT3L4(buf8);
			temp[i][6][0] = buf8[1];
			temp[i][4][0] = buf8[3];
			temp[i][2][0] = buf8[2];
			temp[i][0][0] = buf8[0];

			bufa[1][0] = (filter[6]*work[6+i][1]);
			bufa[1][1] = (filter[5]*(work[5+i][1]+work[7+i][1]));
			bufa[1][2] = -(filter[0]*(work[0+i][1]+work[12+i][1]))+(filter[4]*(work[4+i][1]+work[8+i][1]));
			bufa[1][3] = -(filter[1]*(work[1+i][1]+work[11+i][1]))+(filter[3]*(work[3+i][1]+work[9+i][1]));

			bufa[0][0] = (filter[5]*(work[7+i][0]-work[5+i][0]));
			bufa[0][1] = (filter[0]*(work[12+i][0]-work[0+i][0]))+(filter[4]*(work[8+i][0]-work[4+i][0]));
			bufa[0][2] = (filter[1]*(work[11+i][0]-work[1+i][0]))+(filter[3]*(work[9+i][0]-work[3+i][0]));
			bufa[0][3] = (filter[2]*(work[10+i][0]-work[2+i][0]));

			for(n = 0; n<4; n++) {
				buf8[n] = bufa[1][n]+bufa[0][3-n];
			}
			computeDCT3L4(buf8);
			temp[i][7][1] = buf8[0];
			temp[i][5][1] = buf8[2];
			temp[i][3][1] = buf8[3];
			temp[i][1][1] = buf8[1];

			for(n = 0; n<4; n++) {
				buf8[n] = bufa[1][n]-bufa[0][3-n];
			}
			computeDCT3L4(buf8);
			temp[i][6][1] = buf8[1];
			temp[i][4][1] = buf8[3];
			temp[i][2][1] = buf8[2];
			temp[i][0][1] = buf8[0];
		}
	}

	//complex filter, size 12
	private void performChannelFilter12(float[] filter) {
		int n;
		for(int i = 0; i<TIME_SLOTS_RATE; i++) {
			for(n = 0; n<6; n++) {
				if(n==0) {
					bufa[0][0] = work[6+i][0]*filter[6];
					bufa[2][0] = work[6+i][1]*filter[6];
				}
				else {
					bufa[0][6-n] = (work[n+i][0]+work[12-n+i][0])*filter[n];
					bufa[2][6-n] = (work[n+i][1]+work[12-n+i][1])*filter[n];
				}
				bufa[3][n] = (work[n+i][0]-work[12-n+i][0])*filter[n];
				bufa[1][n] = (work[n+i][1]-work[12-n+i][1])*filter[n];
			}

			computeDCT3L6(bufa[0], buf12a[0]);
			computeDCT3L6(bufa[2], buf12b[0]);

			computeDCT3L6(bufa[1], buf12a[1]);
			computeDCT3L6(bufa[3], buf12b[1]);

			for(n = 0; n<6; n += 2) {
				temp[i][n][0] = buf12a[0][n]-buf12a[1][n];
				temp[i][n][1] = buf12b[0][n]+buf12b[1][n];
				temp[i][n+1][0] = buf12a[0][n+1]+buf12a[1][n+1];
				temp[i][n+1][1] = buf12b[0][n+1]-buf12b[1][n+1];

				temp[i][10-n][0] = buf12a[0][n+1]-buf12a[1][n+1];
				temp[i][10-n][1] = buf12b[0][n+1]+buf12b[1][n+1];
				temp[i][11-n][0] = buf12a[0][n]+buf12a[1][n];
				temp[i][11-n][1] = buf12b[0][n]-buf12b[1][n];
			}
		}
	}

	private void computeDCT3L4(float[] f) {
		dctBuf[0] = f[3]-(DCT3_4_TABLE[0]*f[1]);
		dctBuf[1] = (DCT3_4_TABLE[1]*dctBuf[0])+f[1];
		dctBuf[2] = dctBuf[0]-(DCT3_4_TABLE[2]*dctBuf[1]);
		dctBuf[0] = DCT3_4_TABLE[3]*f[2];
		dctBuf[3] = f[0]+dctBuf[0];
		dctBuf[4] = f[0]-dctBuf[0];
		f[0] = dctBuf[3]+dctBuf[1];
		f[3] = dctBuf[3]-dctBuf[1];
		f[2] = dctBuf[4]+dctBuf[2];
		f[1] = dctBuf[4]-dctBuf[2];
	}

	private void computeDCT3L6(float[] in, float[] out) {
		dctBuf[0] = in[3]*DCT3_6_TABLE[0];
		dctBuf[1] = in[0]+dctBuf[0];
		dctBuf[2] = in[0]-dctBuf[0];
		dctBuf[3] = (in[1]-in[5])*DCT3_6_TABLE[1];
		dctBuf[4] = (in[2]*DCT3_6_TABLE[2])+(in[4]*DCT3_6_TABLE[3]);
		dctBuf[5] = dctBuf[4]-in[4];
		dctBuf[6] = (in[1]*DCT3_6_TABLE[4])+(in[5]*DCT3_6_TABLE[5]);
		dctBuf[7] = dctBuf[6]-dctBuf[3];
		out[0] = dctBuf[1]+dctBuf[6]+dctBuf[4];
		out[1] = dctBuf[2]+dctBuf[3]-in[4];
		out[2] = dctBuf[7]+dctBuf[2]-dctBuf[5];
		out[3] = dctBuf[1]-dctBuf[7]-dctBuf[5];
		out[4] = dctBuf[1]-dctBuf[3]-in[4];
		out[5] = dctBuf[2]-dctBuf[6]+dctBuf[4];
	}
}
