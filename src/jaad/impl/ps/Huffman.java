package jaad.impl.ps;

import jaad.AACException;
import jaad.impl.BitStream;

class Huffman {

	public static void decode(BitStream in, boolean time, int pars, int[][] huffT, int[][] huffF, int[] par) throws AACException {
		int n;
		if(time) {
			for(n = 0; n<pars; n++) {
				par[n] = decodeHuffman(in, huffT);
			}
		}
		else {
			par[0] = decodeHuffman(in, huffF);
			for(n = 1; n<pars; n++) {
				par[n] = decodeHuffman(in, huffF);
			}
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
