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
 * Generates lookup tables for standard inverse quantization by the formulas:
 * <code>f(x) = x<sup>4/3</sup></code>
 * and
 * <code>f(x) = 2<sup>0.25*(x-100)</sup></code>
 * @author in-somnia
 */
public class InvQuantTables {

	private static final int LENGTH = 8191;
	private static final float FOUR_THIRD = 4.0f/3.0f;

	public static void main(String[] args) {
		Utils.printTable(generateIQTable(), "iq table");
		Utils.printTable(generateGainTable(), "gain table");
	}

	private static float[] generateIQTable() {
		float[] f = new float[LENGTH];
		for(int i = 0; i<f.length; i++) {
			f[i] = (float) Math.pow(i, FOUR_THIRD);
		}
		return f;
	}

	private static float[] generateGainTable() {
		float[] f = new float[256];
		for(int i = 0; i<f.length; i++) {
			f[i] = (float) Math.pow(2.0, 0.25*(i-100));
		}
		return f;
	}
}
