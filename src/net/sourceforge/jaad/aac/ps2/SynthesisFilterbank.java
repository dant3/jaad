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
package net.sourceforge.jaad.aac.ps2;

//hybrid synthesis filterbank: sums lower frequency bands
class SynthesisFilterbank {

	//in: 91 x 32 complex, out: 64 x 32 complex for SBR
	public static void process(float[][][] in, float[][][] out, boolean use34) {
		int n, k;
		if(use34) {
			for(n = 0; n<32; n++) {
				//sum first 32 into 5
				for(k = 0; k<5; k++) {
					out[k][n][0] = 0;
					out[k][n][1] = 0;
				}
				for(k = 0; k<12; k++) {
					out[0][n][0] += in[k][n][0];
					out[0][n][1] += in[k][n][1];
				}
				for(k = 12; k<19; k++) {
					out[1][n][0] += in[k][n][0];
					out[1][n][1] += in[k][n][1];
				}
				for(k = 20; k<24; k++) {
					out[2][n][0] += in[k][n][0];
					out[2][n][1] += in[k][n][1];
					out[3][n][0] += in[k+4][n][0];
					out[3][n][1] += in[k+4][n][1];
					out[4][n][0] += in[k+8][n][0];
					out[4][n][1] += in[k+8][n][1];
				}
				//copy remaining 59
				for(k = 0; k<59; k++) {
					out[k+5][n][0] = in[k+32][n][0];
					out[k+5][n][1] = in[k+32][n][1];
				}
			}
		}
		else {
			for(n = 0; n<32; n++) {
				//sum first 10 into 3
				out[0][n][0] = in[0][n][0]+in[1][n][0]+in[2][n][0]
					+in[3][n][0]+in[4][n][0]+in[5][n][0];
				out[0][n][1] = in[0][n][1]+in[1][n][1]+in[2][n][1]
					+in[3][n][1]+in[4][n][1]+in[5][n][1];
				out[1][n][0] = in[6][n][0]+in[7][n][0];
				out[1][n][1] = in[6][n][1]+in[7][n][1];
				out[2][n][0] = in[8][n][0]+in[9][n][0];
				out[2][n][1] = in[8][n][1]+in[9][n][1];
				//copy remaining 61
				for(k = 0; k<61; k++) {
					out[k+3][n][0] = in[k+10][n][0];
					out[k+3][n][1] = in[k+10][n][1];
				}
			}
		}
	}
}
