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
package tablegen;

/**
 * Generates sin/cos lookup table for FFT.
 * @author in-somnia
 */
public class FFTTables {

	public static void main(String[] args) {
		Utils.printTable(generateFFTTableLong(512), "FFT_TABLE_512");
		Utils.printTable(generateFFTTableShort(64), "FFT_TABLE_64");
		Utils.printTable(generateFFTTableLong(480), "FFT_TABLE_480");
		Utils.printTable(generateFFTTableShort(60), "FFT_TABLE_60");
	}

	private static float[][] generateFFTTableShort(int len) {
		final float t = 2.0f*(float) Math.PI/len;
		final float cosT = (float) Math.cos(t);
		final float sinT = (float) Math.sin(t);
		final float[][] f = new float[len][2];
		f[0][0] = 1.0f;
		f[0][1] = 0.0f;
		float lastImag = 0.0f;

		for(int i = 1; i<len; i++) {
			f[i][0] = f[i-1][0]*cosT+lastImag*sinT;
			lastImag = lastImag*cosT-f[i-1][0]*sinT;
			f[i][1] = -lastImag;
		}

		return f;
	}

	//long table needs forward imaginary parts
	private static float[][] generateFFTTableLong(int len) {
		final float t = 2.0f*(float) Math.PI/len;
		final float cosT = (float) Math.cos(t);
		final float sinT = (float) Math.sin(t);
		final float[][] f = new float[len][3];
		f[0][0] = 1.0f;
		f[0][1] = 0.0f;
		f[0][2] = 0.0f;

		for(int i = 1; i<len; i++) {
			f[i][0] = f[i-1][0]*cosT+f[i-1][2]*sinT;
			f[i][2] = f[i-1][2]*cosT-f[i-1][0]*sinT;
			f[i][1] = -f[i][2];
		}

		return f;
	}
}
