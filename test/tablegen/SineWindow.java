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
 * Generates sine windows for the filterbank.
 * @author in-somnia
 */
public class SineWindow {

	public static void main(String[] args) {
		Utils.printTable(generateSineWindow(1024), "sine window long");
		Utils.printTable(generateSineWindow(128), "sine window short");
	}

	private static float[] generateSineWindow(int len) {
		float[] d = new float[len];
		for(int i = 0; i<len; i++) {
			d[i] = (float) Math.sin((i+0.5)*(Math.PI/(2.0*len)));
		}
		return d;
	}
}
