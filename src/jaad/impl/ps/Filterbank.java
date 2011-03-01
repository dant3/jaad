package jaad.impl.ps;

class Filterbank implements PSConstants, FilterbankTables {

	private static final int FB_LEN = 32;
	private float[][][] buf;

	Filterbank() {
		buf = new float[5][44][2];
	}

	void performAnalysis(float[][][] in, float[][][] out, boolean use34) {
		int i, j;
		for(i = 0; i<5; i++) {
			for(j = 0; j<38; j++) {
				in[i][j+6][0] = buf[j][i][0];
				in[i][j+6][1] = buf[j][i][1];
			}
		}
		if(use34) {
			performChannelFilter4(in[0], out, 0, F34_0_12, 12, FB_LEN);
			performChannelFilter4(in[1], out, 12, F34_1_8, 8, FB_LEN);
			performChannelFilter4(in[2], out, 20, F34_2_4, 4, FB_LEN);
			performChannelFilter4(in[3], out, 24, F34_2_4, 4, FB_LEN);
			performChannelFilter4(in[4], out, 28, F34_2_4, 4, FB_LEN);
			for(i = 0; i<59; i++) {
				for(j = 0; j<FB_LEN; j++) {
					out[i+FB_LEN][j][0] = buf[0][j][i+5];
					out[i+FB_LEN][j][1] = buf[1][j][i+5];
				}
			}
		}
		else {
			performChannelFilter6(in[0], out, 0, F20_0_8, FB_LEN);
			performChannelFilter2(in[1], out, 6, G1_Q2, FB_LEN, true);
			performChannelFilter2(in[2], out, 8, G1_Q2, FB_LEN, false);
			for(i = 0; i<61; i++) {
				for(j = 0; j<FB_LEN; j++) {
					out[i+10][j][0] = buf[0][j][i+3];
					out[i+10][j][1] = buf[1][j][i+3];
				}
			}
		}
		//update in_buf
		for(i = 0; i<5; i++) {
			System.arraycopy(in[i], 0, in[i+FB_LEN], 0, 6);
			//memcpy(in[i], in[i]+32, 6 * sizeof(in[i][0]));
		}
	}

	void performSynthesis(float[][][] in, float[][][] out, boolean use34) {
		int i, n;
		if(use34) {
			for(n = 0; n<FB_LEN; n++) {
				for(i = 0; i<12; i++) {
					out[n][0][0] += in[i][n][0];
					out[n][0][1] += in[i][n][1];
				}
				for(i = 0; i<8; i++) {
					out[n][1][0] += in[12+i][n][0];
					out[n][1][1] += in[12+i][n][1];
				}
				for(i = 0; i<4; i++) {
					out[n][2][0] += in[20+i][n][0];
					out[n][2][1] += in[20+i][n][1];
					out[n][3][0] += in[24+i][n][0];
					out[n][3][1] += in[24+i][n][1];
					out[n][4][0] += in[28+i][n][0];
					out[n][4][1] += in[28+i][n][1];
				}
			}
			for(i = 0; i<59; i++) {
				for(n = 0; n<FB_LEN; n++) {
					out[n][i+5][0] = in[i+FB_LEN][n][0];
					out[n][i+5][1] = in[i+FB_LEN][n][1];
				}
			}
		}
		else {
			for(n = 0; n<FB_LEN; n++) {
				out[n][0][0] = in[0][n][0]+in[1][n][0]+in[2][n][0]
						+in[3][n][0]+in[4][n][0]+in[5][n][0];
				out[n][0][1] = in[0][n][1]+in[1][n][1]+in[2][n][1]
						+in[3][n][1]+in[4][n][1]+in[5][n][1];
				out[n][1][0] = in[6][n][0]+in[7][n][0];
				out[n][1][1] = in[6][n][1]+in[7][n][1];
				out[n][2][0] = in[8][n][0]+in[9][n][0];
				out[n][2][1] = in[8][n][1]+in[9][n][1];
			}
			for(i = 0; i<61; i++) {
				for(n = 0; n<FB_LEN; n++) {
					out[n][i+3][0] = in[i+10][n][0];
					out[n][i+3][1] = in[i+10][n][1];
				}
			}
		}
	}

	/** Split one subband into 2 subsubbands with a symmetric real filter.
	 * The filter must have its non-center even coefficients equal to zero. */
	private void performChannelFilter2(float[][] in, float[][][] out, int outOff, float[] filter, int len, boolean reverse) {
		int inOff = 0;
		int j;
		for(int i = 0; i<len; i++, inOff++) {
			float re_in = filter[6]*in[inOff+6][0];
			float re_op = 0.0f;
			float im_in = filter[6]*in[inOff+6][1];
			float im_op = 0.0f;
			for(j = 0; j<6; j += 2) {
				re_op += filter[j+1]*(in[inOff+j+1][0]+in[inOff+12-j-1][0]);
				im_op += filter[j+1]*(in[inOff+j+1][1]+in[inOff+12-j-1][1]);
			}
			int x = reverse ? 1 : 0;
			out[outOff+x][i][0] = re_in+re_op;
			out[outOff+x][i][1] = im_in+im_op;
			x = reverse ? 0 : 1;
			out[outOff+x][i][0] = re_in-re_op;
			out[outOff+x][i][1] = im_in-im_op;
		}
	}

	private void performChannelFilter4(float[][] in, float[][][] out, int outOff, float[][][] filter, int N, int len) {
		int inOff = 0;
		int i, j, ssb;

		for(i = 0; i<len; i++, inOff++) {
			for(ssb = 0; ssb<N; ssb++) {
				float sum_re = filter[ssb][6][0]*in[inOff+6][0], sum_im = filter[ssb][6][0]*in[inOff+6][1];
				for(j = 0; j<6; j++) {
					float in0_re = in[inOff+j][0];
					float in0_im = in[inOff+j][1];
					float in1_re = in[inOff+12-j][0];
					float in1_im = in[inOff+12-j][1];
					sum_re += filter[ssb][j][0]*(in0_re+in1_re)-filter[ssb][j][1]*(in0_im-in1_im);
					sum_im += filter[ssb][j][0]*(in0_im+in1_im)+filter[ssb][j][1]*(in0_re-in1_re);
				}
				out[outOff+ssb][i][0] = sum_re;
				out[outOff+ssb][i][1] = sum_im;
			}
		}
	}

	/** Split one subband into 6 subsubbands with a complex filter */
	private void performChannelFilter6(float[][] in, float[][][] out, int outOff, float[][][] filter, int len) {
		int i, j, ssb;
		int N = 8;
		float[][] temp = new float[8][2];

		int inOff = 0;
		for(i = 0; i<len; i++, inOff++) {
			for(ssb = 0; ssb<N; ssb++) {
				float sum_re = filter[ssb][6][0]*in[inOff+6][0], sum_im = filter[ssb][6][0]*in[inOff+6][1];
				for(j = 0; j<6; j++) {
					float in0_re = in[inOff+j][0];
					float in0_im = in[inOff+j][1];
					float in1_re = in[inOff+12-j][0];
					float in1_im = in[inOff+12-j][1];
					sum_re += filter[ssb][j][0]*(in0_re+in1_re)-filter[ssb][j][1]*(in0_im-in1_im);
					sum_im += filter[ssb][j][0]*(in0_im+in1_im)+filter[ssb][j][1]*(in0_re-in1_re);
				}
				temp[ssb][0] = sum_re;
				temp[ssb][1] = sum_im;
			}
			out[outOff+0][i][0] = temp[6][0];
			out[outOff+0][i][1] = temp[6][1];
			out[outOff+1][i][0] = temp[7][0];
			out[outOff+1][i][1] = temp[7][1];
			out[outOff+2][i][0] = temp[0][0];
			out[outOff+2][i][1] = temp[0][1];
			out[outOff+3][i][0] = temp[1][0];
			out[outOff+3][i][1] = temp[1][1];
			out[outOff+4][i][0] = temp[2][0]+temp[5][0];
			out[outOff+4][i][1] = temp[2][1]+temp[5][1];
			out[outOff+5][i][0] = temp[3][0]+temp[4][0];
			out[outOff+5][i][1] = temp[3][1]+temp[4][1];
		}
	}
}
