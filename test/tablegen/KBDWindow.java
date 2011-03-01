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
 * Generates kaiser-bessel-derived windows for the filterbank.
 * @author in-somnia
 */
public class KBDWindow {

	private static final int ITERATIONS = 50;

	public static void main(String[] args) {
		Utils.printTable(generateKBDWindow(4.0f, 1024), "kbd window long");
		Utils.printTable(generateKBDWindow(6.0f, 128), "kbd window short");
	}

	private static float[] generateKBDWindow(float alpha, int len) {
		final float PIN = (float)Math.PI/len;
		float[] out = new float[len];
		int n, j;
		float sum = 0.0f, bessel, tmp;
		float[] f = new float[len];
		final float alpha2 = (alpha*PIN)*(alpha*PIN);

		for(n = 0; n<len; n++) {
			tmp = n*(len-n)*alpha2;
			bessel = 1.0f;
			for(j = ITERATIONS; j>0; j--) {
				bessel = bessel*tmp/(j*j)+1;
			}
			sum += bessel;
			f[n] = sum;
		}

		sum++;
		for(n = 0; n<len; n++) {
			out[n] = (float) Math.sqrt(f[n]/sum);
		}
		return out;
	}
}
