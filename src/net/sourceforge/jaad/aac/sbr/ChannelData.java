package net.sourceforge.jaad.aac.sbr;

import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;

class ChannelData implements Constants, HuffmanTables {

	private final int channel;
	private int ampRes;
	private int frameClass;
	private int numEnv, varBord0, varBord1, numRel0, numRel1, numNoise, pointer;
	private int[] freqRes, relBord0, relBord1;
	//private int freqResPrev;
	private int[] dfEnv, dfNoise, invfMode;
	private int[][] dataEnv, dataNoise;
	//private float[] dataEnvPrev, dataNoisePrev;
	private boolean harmonicsPresent;
	private boolean[] addHarmonics;

	ChannelData(int channel) {
		this.channel = channel;
	}

	void decodeGrid(BitStream in, Header header) throws AACException {
		ampRes = header.getAmpRes();

		frameClass = in.readBits(2);
		switch(frameClass) {
			case FIXFIX:
				numEnv = 1<<in.readBits(2);
				if(numEnv==1) ampRes = 0;

				freqRes = new int[numEnv];
				freqRes[0] = in.readBit();
				for(int env = 1; env<numEnv; env++) {
					freqRes[env] = freqRes[0];
				}
				break;
			case FIXVAR:
				varBord1 = in.readBits(2);
				numRel1 = in.readBits(2);
				numEnv = numRel1+1;

				relBord1 = new int[numEnv-1];
				for(int rel = 0; rel<numEnv-1; rel++) {
					relBord1[rel] = 2*in.readBits(2)+2;
				}

				pointer = in.readBits(CEIL_LOG2[numEnv]);

				freqRes = new int[numEnv];
				for(int env = 0; env<numEnv; env++) {
					freqRes[numEnv-1-env] = in.readBit();
				}
				break;
			case VARFIX:
				varBord0 = in.readBits(2);
				numRel0 = in.readBits(2);
				numEnv = numRel0+1;

				relBord0 = new int[numEnv-1];
				for(int rel = 0; rel<numEnv-1; rel++) {
					relBord0[rel] = 2*in.readBits(2)+2;
				}

				pointer = in.readBits(CEIL_LOG2[numEnv]);

				freqRes = new int[numEnv];
				for(int env = 0; env<numEnv; env++) {
					freqRes[env] = in.readBit();
				}
				break;
			case VARVAR:
				varBord0 = in.readBits(2);
				varBord1 = in.readBits(2);
				numRel0 = in.readBits(2);
				numRel1 = in.readBits(2);
				numEnv = numRel0+numRel1+1;

				relBord0 = new int[numRel0];
				for(int rel = 0; rel<numRel0; rel++) {
					relBord0[rel] = 2*in.readBits(2)+2;
				}
				relBord1 = new int[numRel1];
				for(int rel = 0; rel<numRel1; rel++) {
					relBord1[rel] = 2*in.readBits(2)+2;
				}

				pointer = in.readBits(CEIL_LOG2[numEnv]);

				freqRes = new int[numEnv];
				for(int env = 0; env<numEnv; env++) {
					freqRes[env] = in.readBit();
				}
				break;
		}

		if(numEnv>1) numNoise = 2;
		else numNoise = 1;
	}

	void copyGrid(ChannelData cd) {
		ampRes = cd.ampRes;
		frameClass = cd.frameClass;
		numEnv = cd.numEnv;
		numNoise = cd.numNoise;

		freqRes = new int[cd.freqRes.length];
		System.arraycopy(cd.freqRes, 0, freqRes, 0, numEnv);
		//System.arraycopy(cd.getTe(), 0, te, 0, te.length);
		//System.arraycopy(cd.getTq(), 0, tq, 0, tq.length);

		pointer = cd.pointer;
	}

	void decodeDTDF(BitStream in) throws AACException {
		dfEnv = new int[numEnv];
		for(int env = 0; env<numEnv; env++) {
			dfEnv[env] = in.readBit();
		}

		dfNoise = new int[numNoise];
		for(int noise = 0; noise<numNoise; noise++) {
			dfNoise[noise] = in.readBit();
		}
	}

	void decodeInvF(BitStream in, FrequencyTables tables) throws AACException {
		final int numNoiseBands = tables.getNq();
		invfMode = new int[numNoiseBands];
		for(int i = 0; i<numNoiseBands; i++) {
			invfMode[i] = in.readBits(2);
		}
	}

	void copyInvF(ChannelData cd) {
		invfMode = new int[cd.invfMode.length];
		System.arraycopy(cd.invfMode, 0, invfMode, 0, invfMode.length);
	}

	void decodeEnvelope(BitStream in, FrequencyTables tables, boolean coupling) throws AACException {
		final int bits = 7-((coupling&&(channel==1)) ? 1 : 0)-ampRes;

		int delta;
		int[][] huffT, huffF;
		if(coupling&&(channel==1)) {
			delta = 1;
			if(ampRes==1) {
				huffT = T_HUFFMAN_ENV_BAL_3_0DB;
				huffF = F_HUFFMAN_ENV_BAL_3_0DB;
			}
			else {
				huffT = T_HUFFMAN_ENV_BAL_1_5DB;
				huffF = F_HUFFMAN_ENV_BAL_1_5DB;
			}
		}
		else {
			delta = 0;
			if(ampRes==1) {
				huffT = T_HUFFMAN_ENV_3_0DB;
				huffF = F_HUFFMAN_ENV_3_0DB;
			}
			else {
				huffT = T_HUFFMAN_ENV_1_5DB;
				huffF = F_HUFFMAN_ENV_1_5DB;
			}
		}

		int j;
		dataEnv = new int[numEnv][];
		for(int i = 0; i<numEnv; i++) {
			int numEnvBands = tables.getN()[freqRes[i]];
			dataEnv[i] = new int[numEnvBands];
			if(dfEnv[i]==1) {
				for(j = 0; j<numEnvBands; j++) {
					dataEnv[i][j] = decodeHuffman(in, huffT)<<delta;
				}
			}
			else {
				dataEnv[0][i] = in.readBits(bits)<<delta;

				for(j = 1; j<numEnvBands; j++) {
					dataEnv[i][j] = decodeHuffman(in, huffF)<<delta;
				}
			}
		}
	}

	void decodeNoise(BitStream in, FrequencyTables tables, boolean coupling) throws AACException {
		int delta;
		int[][] huffT, huffF;
		if(coupling&&(channel==1)) {
			delta = 1;
			huffT = T_HUFFMAN_NOISE_BAL_3_0DB;
			huffF = F_HUFFMAN_ENV_BAL_3_0DB;
		}
		else {
			delta = 0;
			huffT = T_HUFFMAN_NOISE_3_0DB;
			huffF = F_HUFFMAN_ENV_3_0DB;
		}

		final int len = tables.getNq();
		int j;
		dataNoise = new int[numNoise][len];
		for(int i = 0; i<numNoise; i++) {
			if(dfNoise[i]==1) {
				for(j = 0; j<len; j++) {
					dataNoise[i][j] = decodeHuffman(in, huffT)<<delta;
				}
			}
			else {
				dataNoise[0][i] = in.readBits(5)<<delta;
				for(j = 1; j<len; j++) {
					dataNoise[i][j] = decodeHuffman(in, huffF)<<delta;
				}
			}
		}
	}

	void decodeSinusoidals(BitStream in, FrequencyTables tables) throws AACException {
		addHarmonics = new boolean[tables.getNHigh()];

		harmonicsPresent = in.readBool();
		if(harmonicsPresent) {
			for(int n = 0; n<addHarmonics.length; n++) {
				addHarmonics[n] = in.readBool();
			}
		}
		else Arrays.fill(addHarmonics, false);
	}

	private int decodeHuffman(BitStream in, int[][] table) throws AACException {
		int index = 0;
		int bit;
		while(index>=0) {
			bit = in.readBit();
			index = table[index][bit];
		}
		return index+HUFFMAN_OFFSET;
	}
}
