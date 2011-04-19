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
package net.sourceforge.jaad.impl.ps;

import net.sourceforge.jaad.AACException;
import net.sourceforge.jaad.impl.BitStream;

class Huffman {

	static void decode(BitStream in, int[][] table, int[] out, int len) throws AACException {
		for(int i = 0; i<len; i++) {
			out[i] = decodeHuffman(in, table);
		}
	}

	private static int decodeHuffman(BitStream in, int[][] table) throws AACException {
		int bit;
		int index = 0;

		while(index>=0) {
			bit = in.readBit();
			index = table[index][bit];
		}

		return index+31;
	}
}
