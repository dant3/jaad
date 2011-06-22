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

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;

public class PS implements PSConstants, PSTables, HuffmanTables {

	//hedaer
	private boolean headerEnabled;
	private final PSHeader header;
	//bitstream variables
	private boolean frameClass;
	private int envCount, envCountPrev;
	private final int[] borderPositions;
	//pars
	private final int[][] iidPars, iccPars, ipdPars, opdPars;
	//dequantized values
	private final int[][] iid; //TODO: using float here too would make it much easier!
	private final float[][] icc, ipd, opd;
	//working buffer
	private final float[][][] s;
	private float[][][] H; //TODO: sizes

	public PS() {
		headerEnabled = false;
		header = new PSHeader();

		borderPositions = new int[MAX_ENVELOPES];

		iidPars = new int[MAX_ENVELOPES][MAX_IID_ICC_PARS];
		iccPars = new int[MAX_ENVELOPES][MAX_IID_ICC_PARS];
		ipdPars = new int[MAX_ENVELOPES][MAX_IPD_OPD_PARS];
		opdPars = new int[MAX_ENVELOPES][MAX_IPD_OPD_PARS];

		iid = new int[MAX_ENVELOPES][MAX_IID_ICC_PARS];
		icc = new float[MAX_ENVELOPES][MAX_IID_ICC_PARS];
		ipd = new float[MAX_ENVELOPES][MAX_IID_ICC_PARS];
		opd = new float[MAX_ENVELOPES][MAX_IID_ICC_PARS];

		s = new float[91][32][2];
	}

	/*========================= decoding =========================*/
	public void decode(BitStream in) throws AACException {
		if(headerEnabled = in.readBool()) header.decode(in);

		frameClass = in.readBool();
		//envelopes (table 8.29)
		final int envIdx = in.readBits(2);
		envCountPrev = envCount;
		envCount = envIdx+(frameClass ? 1 : 0);
		if(envIdx==3&&!frameClass) envCount++;
		//if envCount==0: no new parameters, use old ones
		if(envCount==0) envCount = envCountPrev;

		//border positions
		int e;
		if(frameClass) {
			for(e = 0; e<envCount; e++) {
				borderPositions[e] = in.readBits(5);
			}
		}

		int len;
		boolean dt;
		int[][] table;
		//iid
		if(header.isIIDEnabled()) {
			len = header.getIIDPars();
			final boolean fine = header.useIIDQuantFine();
			final int[] quant = fine ? IID_QUANT_FINE : IID_QUANT_DEFAULT;
			for(e = 0; e<envCount; e++) {
				dt = in.readBool();
				table = dt ? (fine ? HUFF_IID_FINE_DT : HUFF_IID_DEFAULT_DT)
					: (fine ? HUFF_IID_FINE_DF : HUFF_IID_DEFAULT_DF);
				decodePars(in, table, iidPars, e, len, dt, false);
				dequant(iidPars[e], iid[e], len, quant);
			}
		}

		//icc
		if(header.isICCEnabled()) {
			len = header.getICCPars();
			for(e = 0; e<envCount; e++) {
				dt = in.readBool();
				table = dt ? HUFF_ICC_DT : HUFF_ICC_DF;
				decodePars(in, table, iccPars, e, len, dt, false);
				dequant(iccPars[e], icc[e], len, ICC_QUANT);
			}
		}

		//extension
		if(header.isExtEnabled()) {
			int left = in.readBits(4);
			if(left==15) left += in.readBits(8);
			left *= 8;

			int id;
			while(left>7) {
				id = in.readBits(2);
				left -= 2;
				left -= decodeExtension(in, id);
			}

			in.skipBits(left);
		}
	}

	private void decodePars(BitStream in, int[][] table, int[][] pars, int env, int len, boolean dt, boolean mod) throws AACException {
		//huffman delta decoding
		if(dt) {
			final int prev = (env>0) ? env-1 : envCountPrev-1;
			for(int i = 0; i<len; i++) {
				pars[env][i] = pars[prev][i]+decodeHuffman(in, table);
				if(mod) pars[env][i] &= 7;
			}
		}
		else {
			pars[env][0] = decodeHuffman(in, table);
			for(int i = 1; i<len; i++) {
				pars[env][i] = pars[env][i-1]+decodeHuffman(in, table);
				if(mod) pars[env][i] &= 7;
			}
		}
	}

	private int decodeHuffman(BitStream in, int[][] table) throws AACException {
		int off = 0;
		int len = table[off][0];
		int cw = in.readBits(len);
		int j;
		while(cw!=table[off][1]) {
			off++;
			j = table[off][0]-len;
			len = table[off][0];
			cw <<= j;
			cw |= in.readBits(j);
		}
		return table[off][2];
	}

	private int decodeExtension(BitStream in, int id) throws AACException {
		final int start = in.getPosition();

		if(id==0) {
			//ipdopd
			final boolean b = in.readBool();
			header.setIPDOPDEnabled(b);
			if(b) {
				final int len = header.getIPDOPDPars();
				boolean dt;
				int[][] table;

				for(int e = 0; e<envCount; e++) {
					dt = in.readBool();
					table = dt ? HUFF_IPD_DT : HUFF_IPD_DF;
					decodePars(in, table, ipdPars, e, len, dt, true);
					dequant(ipdPars[e], ipd[e], len, dt ? IPD_OPD_QUANT : ICC_QUANT);

					dt = in.readBool();
					table = dt ? HUFF_OPD_DT : HUFF_OPD_DF;
					decodePars(in, table, opdPars, e, len, dt, true);
					dequant(opdPars[e], opd[e], len, dt ? IPD_OPD_QUANT : ICC_QUANT);
				}
			}
			in.skipBit(); //reserved
		}

		return in.getPosition()-start;
	}

	/*========================= dequantization=========================*/
	private void dequant(int[] pars, int[] vals, int len, int[] table) {
		for(int i = 0; i<len; i++) {
			vals[i] = table[pars[i]];
		}
	}

	private void dequant(int[] pars, float[] vals, int len, float[] table) {
		for(int i = 0; i<len; i++) {
			vals[i] = table[pars[i]];
		}
	}

	/*========================= processing =========================*/
	public boolean hasHeader() {
		return headerEnabled;
	}

	//in: 64 x 38 complex from SBR, left/right: 2048 output time samples
	public void process(float[][][] in, float[] left, float[] right) {
		//1. hybrid analysis (in -> buf)
		AnalysisFilterbank.process(in, s, header.use34Bands());

		//2. decorrelation
		//3. stereo processing
		//4. hybrid synthesis
	}

	private void decorrelate() {
		final int nL = 32;
		final int mode = header.getBandMode();
		int i;

		//calculate decorrelated signal
		//TODO...

		//transient detection
		final int len = PAR_BANDS[mode];
		final int[] map = K_TO_BK[mode];
		final float[][] power = new float[len][nL];
		int b;
		for(int n = 0; n<nL; n++) {
			for(i = 0; i<PAR_BANDS[mode]; i++) {
				b = map[i];
				power[b][n] += s[i][n][0]*s[i][n][0]+s[i][n][1]*s[i][n][1];
			}
		}

		final float[][] peakDecayNrg = new float[len][nL];
		for(i = 0; i<PAR_BANDS[mode]; i++) {
			for(int n = 0; n<nL; n++) {
			}
		}
	}
}
