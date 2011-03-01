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
 * Generates lookup table for Intensity Stereo by the formula:
 * <code>f(x) = 0.5<sup>0.25*x</sup></code>
 * @author in-somnia
 */
public class ISScale {

	private static final int LENGTH = 255;

	public static void main(String[] args) {
		Utils.printTable(generateISScaleTable(), "is scale");
	}

	private static float[] generateISScaleTable() {
		final float[] f = new float[LENGTH];
		for(int i = 0; i<LENGTH; i++) {
			f[i] = (float) Math.pow(0.5, (0.25*i));
		}
		return f;
	}
}
