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
		//TODO: optimize loop
		for(int n = 0; n<128; n++) {
			for(int k = 0; k<64; k++) {
				tmp = Math.PI/128.0*(k+0.5)*(2*n-255);
				COEFS[n][k][0] = gain*(float) Math.cos(tmp);
				COEFS[n][k][1] = gain*(float) Math.sin(tmp);
			}
		}
	}

	//in: 64 x 32 complex, out: 2048 time samples
	public void process(float[][][] in, float[] out, int ch) {
		int n, k, off = 0;

		//each loop creates 64 output samples
		for(int l = 0; l<TIME_SLOTS_RATE; l++) {
			//1. shift buffer
			System.arraycopy(v[ch], 0, v[ch], 128, 1152);

			//2. multiple input by matrix and save in buffer
			for(n = 0; n<128; n++) {
				v[ch][n] = 0.0f;
				for(k = 0; k<64; k++) {
					v[ch][l] += in[k][l][0]*COEFS[n][k][0];
					v[ch][l] -= in[k][l][1]*COEFS[n][k][1];
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
			for(k = 0; k<64; k++) {
				out[off+k] = g[k];
				for(n = 1; n<10; n++) {
					out[off+k] += g[64*n+k];
				}
			}
			off += 64;
		}
	}
}
