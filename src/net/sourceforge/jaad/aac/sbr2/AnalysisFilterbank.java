package net.sourceforge.jaad.aac.sbr2;

public class AnalysisFilterbank implements SBRConstants, FilterbankTables {

	private final float[][][] COEFS;
	private final float[][] x;
	private final float[] z, y;

	public AnalysisFilterbank() {
		x = new float[2][320]; //for both channels
		z = new float[320]; //tmp buffer
		y = new float[64]; //tmp buffer
		
		//complex coefficients:
		COEFS = new float[32][64][2];
		double tmp;
		for(int k = 0; k<32; k++) {
			for(int n = 0; n<64; n++) {
				tmp = (Math.PI/64)*(k+0.5)*(2*n-0.5);
				COEFS[k][n][0] = (float) (2*Math.cos(tmp));
				COEFS[k][n][1] = (float) (2*Math.sin(tmp));
			}
		}
	}

	//in: 1024 time samples, out: 32 subbands x 32 complex samples
	public void calculate(float[] in, float[][][] out, int ch, int maxBands) {
		int i, j, off = 0;
		for(int l = 0; l<TIME_SLOTS_RATE; l++) {
			//1. shift buffer
			System.arraycopy(x[ch], 0, x[ch], 32, 288);
			//2. add new samples
			for(i = 31; i>=0; i--) {
				x[ch][i] = in[off+31-i];
			}
			off += 32;
			//3. windowing
			for(i = 0; i<320; i++) {
				//TODO: convert WINDOW to floats
				z[i] = x[ch][i]*(float) WINDOW[2*i];
			}
			//4. sum samples
			for(i = 0; i<64; i++) {
				y[i] = z[i];
				for(j = 1; j<5; j++) {
					y[i] += z[i+j*64];
				}
			}
			//5. calculate subband samples, TODO: replace with FFT?
			for(i = 0; i<32&&i<maxBands; i++) {
				out[l][i][0] = 0.0f;
				out[l][i][1] = 0.0f;
				for(j = 0; j<64; j++) {
					out[l][i][0] += y[j]*COEFS[i][j][0];
					out[l][i][1] += y[j]*COEFS[i][j][1];
				}
			}
		}
	}
}
