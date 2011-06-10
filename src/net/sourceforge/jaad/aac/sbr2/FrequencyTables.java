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
package net.sourceforge.jaad.aac.sbr2;

import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.SampleFrequency;

//stores and calculates frequency tables
class FrequencyTables implements SBRConstants, SBRTables {

	private int k0, k2;
	//master
	private int[] mft;
	private int nMaster;
	//frequency tables
	private final int[][] fTable;
	private final int[] n;
	private int m, kx, kxPrev;
	//noise table
	private int[] fNoise;
	private int nq;
	//limiter table
	private int[] fLim;
	private int nl;
	//patches
	private int patchCount;
	private int[] patchSubbands, patchStartSubband, patchBorders;

	FrequencyTables() {
		n = new int[2];
		fTable = new int[2][];
	}

	void calculate(SBRHeader header, int sampleRate) throws AACException {
		calculateMFT(header, sampleRate);
		calculateFrequencyTables(header);
		calculateNoiseTable(header);
	}

	private void calculateMFT(SBRHeader header, int sampleRate) throws AACException {
		//lower border k0
		final int sfIndex = SampleFrequency.forFrequency(sampleRate).getIndex();
		final int sfOff = MFT_SF_OFFSETS[sfIndex];
		k0 = MFT_START_MIN[sfIndex]+MFT_START_OFFSETS[sfOff][header.getStartFrequency(false)];
		//higher border k2
		final int stop = header.getStopFrequency(false);
		final int x;
		if(stop==15) x = 3*k0;
		else if(stop==14) x = 2*k0;
		else x = MFT_STOP_MIN[sfIndex]+MFT_STOP_OFFSETS[sfOff][header.getStopFrequency(false)-1];
		k2 = Math.min(64, x);

		if(k0>=k2) throw new AACException("SBR: MFT borders out of range: lower="+k0+", higher="+k2);
		//check requirement (4.6.18.3.6):
		final int max;
		if(sampleRate==44100) max = 35;
		else if(sampleRate>=48000) max = 32;
		else max = 48;
		if((k2-k0)>max) throw new AACException("SBR: too many subbands: "+(k2-k0)+", maximum number for samplerate "+sampleRate+": "+max);

		//MFT calculation
		final int freqScale = header.getFrequencyScale(false);
		if(freqScale==0) calculateMFT1(header, k0, k2);
		else calculateMFT2(header, k0, k2);

		//check requirement (4.6.18.3.6):
		if(header.getXOverBand(false)>=nMaster) throw new AACException("SBR: illegal length of master frequency table: "+nMaster+", xOverBand: "+header.getXOverBand(false));
	}

	//MFT calculation if frequencyScale==0
	private void calculateMFT1(SBRHeader header, int k0, int k2) throws AACException {
		final int dk;
		if(header.isAlterScale(false)) {
			dk = 2;
			nMaster = Math.round((float) (k2-k0)/4.0f)<<1;
		}
		else {
			dk = 1;
			nMaster = (int) ((float) (k2-k0)/2.0f)<<1;
		}
		//check requirement (4.6.18.6.3):
		if(nMaster<=0) throw new AACException("SBR: illegal number of bands for master frequency table: "+nMaster);

		final int k2Achieved = k0+nMaster*dk;
		int k2Diff = k2-k2Achieved;

		final int[] vDk = new int[nMaster];
		Arrays.fill(vDk, dk);

		if(k2Diff!=0) {
			final int incr = (k2Diff>0) ? -1 : 1;
			int k = (k2Diff>0) ? nMaster-1 : 0;
			while(k2Diff!=0) {
				vDk[k] -= incr;
				k += incr;
				k2Diff += incr;
			}
		}

		mft = new int[nMaster+1];
		mft[0] = k0;
		for(int i = 1; i<=nMaster; i++) {
			mft[i] = mft[i-1]+vDk[i-1];
		}
	}

	//MFT calculation if frequencyScale>0
	private void calculateMFT2(SBRHeader header, int k0, int k2) throws AACException {
		final int bands = MFT_INPUT1[header.getFrequencyScale(false)-1];
		final double warp = MFT_INPUT2[header.isAlterScale(false) ? 1 : 0];

		final double div1 = (double) k2/(double) k0;
		final boolean twoRegions;
		final int k1;
		if(div1>2.2449) {
			twoRegions = true;
			k1 = 2*k0;
		}
		else {
			twoRegions = false;
			k1 = k2;
		}

		final double div2 = (double) k1/(double) k0;
		double log = Math.log(div2)*Math.log(2*LOG2);
		final int bandCount0 = 2*(int) Math.round(bands*log);
		//check requirement (4.6.18.6.3):
		if(bandCount0<=0) throw new AACException("SBR: illegal band count for master frequency table: "+bandCount0);

		final int[] vDk0 = new int[bandCount0];
		double pow1, pow2;
		for(int i = 0; i<bandCount0; i++) {
			pow1 = Math.pow(div2, (double) (i+1)/bandCount0);
			pow2 = Math.pow(div2, (double) i/bandCount0);
			vDk0[i] = (int) (Math.round(k0*pow1)-Math.round(k0*pow2));
			//check requirement (4.6.18.6.3):
			if(vDk0[i]<=0) throw new AACException("SBR: illegal value in master frequency table: "+vDk0[i]);
		}
		Arrays.sort(vDk0);

		final int[] vk0 = new int[bandCount0+1];
		vk0[0] = k0;
		for(int i = 1; i<=bandCount0; i++) {
			vk0[i] = vk0[i-1]+vDk0[i-1];
		}

		if(twoRegions) {
			log = Math.log(div1);
			final int bandCount1 = 2*(int) Math.round(bands*log/(2*LOG2*warp));
			final int[] vDk1 = new int[bandCount1];
			int min = 0;
			for(int i = 0; i<bandCount1; i++) {
				pow1 = Math.pow(div1, (double) (i+1)/bandCount1);
				pow2 = Math.pow(div1, (double) i/bandCount1);
				vDk1[i] = (int) (k1*pow1)-(int) (k1*pow2);
				if(vDk1[i]>min) min = vDk1[i];
				//check requirement (4.6.18.6.3):
				else if(vDk1[i]<=0) throw new AACException("SBR: illegal value in master frequency table: "+vDk1[i]);
			}

			if(min<vDk0[vDk0.length-1]) {
				Arrays.sort(vDk1);
				int change = vDk0[vDk0.length-1]-vDk1[0];
				final int x = (int) (vDk1[bandCount1-1]-(double) vDk1[0]/2.0);
				if(change>x) change = x;
				vDk1[0] += change;
				vDk1[bandCount1-1] -= change;
			}

			Arrays.sort(vDk1);
			final int[] vk1 = new int[bandCount1+1];
			vk1[0] = k1;
			for(int i = 1; i<=bandCount1; i++) {
				vk1[i] = vk1[i-1]+vDk1[i-1];
			}

			nMaster = bandCount0+bandCount1;
			mft = new int[nMaster+1];
			System.arraycopy(vk0, 0, mft, 0, bandCount0+1);
			System.arraycopy(vk1, 1, mft, bandCount0+1, bandCount1);
		}
		else {
			nMaster = bandCount0;
			mft = new int[nMaster+1];
			System.arraycopy(vk0, 0, mft, 0, nMaster+1);
		}
	}

	private void calculateFrequencyTables(SBRHeader header) throws AACException {
		final int xover = header.getXOverBand(false);
		n[HIGH] = getNMaster()-xover;
		fTable[HIGH] = new int[n[HIGH]+1];
		System.arraycopy(mft, xover, fTable[HIGH], 0, n[HIGH]+1);

		kxPrev = kx;
		kx = fTable[HIGH][0];
		m = fTable[HIGH][getN(HIGH)]-kx;
		//check requirements (4.6.18.3.6):
		if(kx>32) throw new AACException("SBR: start frequency border out of range: "+kx);
		if((kx+m)>64) throw new AACException("SBR: stop frequency border out of range: "+(kx+m));

		final int half = (int) ((double) n[HIGH]/2.0);
		n[LOW] = half+(n[HIGH]-2*half);
		fTable[LOW] = new int[n[LOW]+1];
		fTable[LOW][0] = 0;
		final int div = (n[HIGH]%2!=0) ? 1 : 0;
		for(int i = 0; i<=n[LOW]; i++) {
			fTable[LOW][i] = fTable[HIGH][2*i-div];
		}
	}

	private void calculateNoiseTable(SBRHeader header) throws AACException {
		final double log = Math.log((double) k2/(double) kx)/LOG2;
		final int x = (int) Math.round(header.getNoiseBands(false)*log);
		nq = Math.max(1, x);
		//check requirement (4.6.18.6.3):
		if(nq>5) throw new AACException("SBR: too many noise floor scalefactors: "+nq);

		fNoise = new int[nq+1];
		fNoise[0] = 0;
		double d1, d2;
		for(int i = 1; i<=nq; i++) {
			d1 = (double) (n[LOW]-fNoise[i-1]);
			d2 = (double) (nq+1-i);
			fNoise[i] = fNoise[i-1]+(int) (d1/d2);
		}
	}

	void calculateLimiterTable(SBRHeader header, int patchCount, int[] patchSubbands, int[] patchStartSubband) throws AACException {
		//called from HFGenerator after calculating patch subbands
		//check requirement (4.6.18.6.3):
		if(patchCount>5) throw new AACException("SBR: too many patches: "+patchCount);
		//save parameters
		this.patchCount = patchCount;
		this.patchSubbands = patchSubbands;
		this.patchStartSubband = patchStartSubband;

		//construct patch borders
		final int bands = header.getLimiterBands();
		if(bands==0) {
			fLim = new int[]{fTable[LOW][0], fTable[LOW][n[LOW]]};
			nl = 1;
			patchBorders = new int[0];
		}
		else {
			//bands>0
			final double limBands = LIM_BANDS_PER_OCTAVE[bands-1];

			patchBorders = new int[patchCount+1];
			patchBorders[0] = kx;
			for(int i = 1; i<=patchCount; i++) {
				patchBorders[i] = patchBorders[i-1]+patchSubbands[i-1];
			}

			fLim = new int[n[LOW]+patchCount];
			System.arraycopy(fTable[LOW], 0, fLim, 0, n[LOW]+1);
			System.arraycopy(patchBorders, 1, fLim, n[LOW+1], patchCount-1);

			Arrays.sort(fLim);

			int k = 1;
			int lims = n[LOW]+patchCount-1;
			double octaves;
			while(k<=lims) {
				octaves = Math.log((double) fLim[k]/(double) fLim[k-1])/LOG2;
				if((octaves*limBands)<0.49) {
					int r = -1;
					if(fLim[k]==fLim[k-1]) r = k;
					else {
						//is fLim[k] equal to any element in patchBorders?
						boolean found = false;
						for(int i = 0; !found&&i<patchBorders.length; i++) {
							if(patchBorders[i]==fLim[k]) found = true;
						}
						if(found) {
							//is fLim[k-1] equal to any element in patchBorders?
							boolean found2 = false;
							for(int i = 0; !found2&&i<patchBorders.length; i++) {
								if(patchBorders[i]==fLim[k]) found2 = true;
							}
							if(found2) k++;
							else r = k-1;
						}
						else r = k;
					}
					if(r>0) {
						//remove element r from fLim
						final int[] tmp = new int[fLim.length-1];
						System.arraycopy(fLim, 0, tmp, 0, r);
						System.arraycopy(fLim, r+1, tmp, r, tmp.length-r);
						fLim = tmp;
						lims--;
					}
				}
				else k++;
			}
			nl = lims;
		}
	}

	//lower MFT border: k0
	public int getK0() {
		return k0;
	}

	//higher MFT border: k2
	public int getK2() {
		return k2;
	}

	//the master frequency table: fMaster
	public int[] getMFT() {
		return mft;
	}

	//bands in master frequency table: Nmaster
	public int getNMaster() {
		return nMaster;
	}

	//the frequency tables: fTableHigh, fTableLow
	public int[] getFrequencyTable(int i) {
		return fTable[i];
	}

	//bands in frequency tables: Nhigh, Nlow
	public int getN(int i) {
		return n[i];
	}

	//both ns
	public int[] getN() {
		return n;
	}

	//first element of fTableHigh: kx
	public int getKx(boolean previous) {
		return previous ? kxPrev : kx;
	}

	//difference between last and first element of fTableHigh: M
	public int getM() {
		return m;
	}

	//the noise floor frequency table: fTableNoise
	public int[] getNoiseTable() {
		return fNoise;
	}

	//bands in noise table: Nq
	public int getNq() {
		return nq;
	}

	//the limiter frequency band table: fTableLim
	public int[] getLimiterTable() {
		return fLim;
	}

	//bands in limiter table: Nl
	public int getNl() {
		return nl;
	}
}
