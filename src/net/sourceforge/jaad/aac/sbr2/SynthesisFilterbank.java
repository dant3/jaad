package net.sourceforge.jaad.aac.sbr2;

class SynthesisFilterbank implements SBRConstants, FilterbankTables {

	private final float[][][] COEFS;
	private final float[][] v;
	private final float[] g;

	SynthesisFilterbank() {
		v = new float[2][1280]; //for both channels
		g = new float[640]; //tmp buffer
		
		//complex coefficients:
		COEFS = new float[128][64][2];
		final float gain = 1.0f/64.0f;
		double tmp;
		for(int l = 0; l<128; l++) {
			for(int k = 0; k<64; k++) {
				tmp = Math.PI/128.0*(k+0.5)*(2*l-255);
				COEFS[l][k][0] = gain*(float) Math.cos(tmp);
				COEFS[l][k][1] = gain*(float) Math.sin(tmp);
			}
		}
	}

	//in: 32 subbands x 64 samples complex, out: 2048 time samples
	public void process(float[][][] in, float[] out, int ch) {
		int n, k, off = 0;
		for(int l = 0; l<TIME_SLOTS_RATE; l++) {
			//1. shift buffer
			System.arraycopy(v, 0, v, 128, 1152);
			//2. multiple input by matrix and save in buffer
			for(l = 0; l<128; l++) {
				v[ch][l] = 0.0f;
				for(k = 0; k<64; k++) {
					v[ch][l] += in[l][k][0]*COEFS[l][k][0];
					v[ch][l] -= in[l][k][1]*COEFS[l][k][1];
				}
			}
			//3. extract samples
			for(n = 0; n<5; n++) {
				for(k = 0; k<64; k++) {
					g[128*n+k] = v[ch][256*n+k];
					g[128*n+64+k] = v[ch][256*n+192+k];
				}
			}
			//4. window signal
			for(n = 0; n<640; n++) {
				g[n] *= WINDOW[n];
			}
			//5. calculate output samples
			for(n = 0; n<64; n++) {
				out[off+n] = 0.0f;
				for(l = 0; l<10; l++) {
					out[off+n] += +g[64*l+n];
				}
			}
			off += 64;
		}
	}
}
