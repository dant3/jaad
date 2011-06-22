package net.sourceforge.jaad.aac.ps2;

class AnalysisFilterbank implements FilterbankTables {

	//in: 64 x 38 complex, out: 91 x 32 complex
	public static void process(float[][][] in, float[][][] out, boolean use34) {
		if(use34) {
			splitBands4(in[0], out, 0, FILTER_34_12, 12);
			splitBands4(in[1], out, 12, FILTER_34_8, 8);
			splitBands4(in[2], out, 20, FILTER_34_4, 4);
			splitBands4(in[3], out, 24, FILTER_34_4, 4);
			splitBands4(in[4], out, 28, FILTER_34_4, 4);
		}
		else {
			splitBands6(in[0], out, 0);
			splitBands2(in[1], out, 6, true);
			splitBands2(in[2], out, 8, false);
		}
	}

	//type B filtering for 2 bands; in: 38 complex, out:32 complex
	private static void splitBands2(float[][] in, float[][][] out, int outOff, boolean reverse) {
		final float[] tmp1 = new float[2];
		final float[] tmp2 = new float[2];
		int inOff = 0;
		int i, j;
		
		for(i = 0; i<32; i++) {
			tmp1[0] = FILTER_20_2[6]*in[inOff+6][0];
			tmp1[1] = FILTER_20_2[6]*in[inOff+6][1];
			tmp2[0] = 0.0f;
			tmp2[1] = 0.0f;
			for(j = 0; j<6; j += 2) {
				tmp2[0] += FILTER_20_2[j+1]*(in[inOff+j+1][0]+in[inOff+12-j-1][0]);
				tmp2[1] += FILTER_20_2[j+1]*(in[inOff+j+1][1]+in[inOff+12-j-1][1]);
			}
			if(reverse) {
				out[outOff+1][i][0] = tmp1[0]+tmp2[0];
				out[outOff+1][i][1] = tmp1[1]+tmp2[1];
				out[outOff][i][0] = tmp1[0]-tmp2[0];
				out[outOff][i][1] = tmp1[1]-tmp2[1];
			}
			else {
				out[outOff][i][0] = tmp1[0]+tmp2[0];
				out[outOff][i][1] = tmp1[1]+tmp2[1];
				out[outOff+1][i][0] = tmp1[0]-tmp2[0];
				out[outOff+1][i][1] = tmp1[1]-tmp2[1];
			}
			inOff++;
		}
	}

	//type A filtering for 8 bands with summation; in: 38 complex, out:32 complex
	private static void splitBands6(float[][] in, float[][][] out, int outOff) {
		final float[][] tmp = new float[8][2];
		final float[] sum = new float[2];
		int i, j, k;
		int inOff = 0;

		for(i = 0; i<32; i++) {
			for(k = 0; k<8; k++) {
				sum[0] = FILTER_20_8[k][6][0]*in[inOff+6][0];
				sum[1] = FILTER_20_8[k][6][0]*in[inOff+6][1];
				for(j = 0; j<6; j++) {
					sum[0] += FILTER_20_8[k][j][0]*(in[inOff+j][0]+in[inOff+12-j][0])
							-FILTER_20_8[k][j][1]*(in[inOff+j][1]-in[inOff+12-j][1]);
					sum[1] += FILTER_20_8[k][j][0]*(in[inOff+j][1]+in[inOff+12-j][1])
							+FILTER_20_8[k][j][1]*(in[inOff+j][0]-in[inOff+12-j][0]);
				}
				tmp[k][0] = sum[0];
				tmp[k][1] = sum[1];
			}
			out[0][i][0] = tmp[6][0];
			out[0][i][1] = tmp[6][1];
			out[1][i][0] = tmp[7][0];
			out[1][i][1] = tmp[7][1];
			out[2][i][0] = tmp[0][0];
			out[2][i][1] = tmp[0][1];
			out[3][i][0] = tmp[1][0];
			out[3][i][1] = tmp[1][1];
			out[4][i][0] = tmp[2][0]+tmp[5][0];
			out[4][i][1] = tmp[2][1]+tmp[5][1];
			out[5][i][0] = tmp[3][0]+tmp[4][0];
			out[5][i][1] = tmp[3][1]+tmp[4][1];

			inOff++;
		}
	}

	//type A filtering for 4/8/12 bands; in: 38 complex, out:32 complex
	private static void splitBands4(float[][] in, float[][][] out, int outOff, float[][][] filter, int len) {
		final float[] sum = new float[2];
		int i, j, k;
		int inOff = 0;

		for(i = 0; i<len; i++) {
			for(k = 0; k<len; k++) {
				sum[0] = filter[k][6][0]*in[inOff+6][0];
				sum[1] = filter[k][6][0]*in[inOff+6][1];
				for(j = 0; j<6; j++) {
					sum[0] += filter[k][j][0]*(in[inOff+j][0]+in[inOff+12-j][0])
							-filter[k][j][1]*(in[inOff+j][1]-in[inOff+12-j][1]);
					sum[1] += filter[k][j][0]*(in[inOff+j][1]+in[inOff+12-j][1])
							+filter[k][j][1]*(in[inOff+j][0]-in[inOff+12-j][0]);
				}
				out[k][i][0] = sum[0];
				out[k][i][1] = sum[1];
			}
			inOff++;
		}
	}
}
