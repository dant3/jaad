package net.sourceforge.jaad.aac.sbr2;

//stores and calculates frequency tables
import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.SampleFrequency;

class FrequencyTables implements SBRTables {

	private static final double LOG2 = 0.6931471805599453;
	private int[] mft;

	FrequencyTables() {
	}

	void calculate(SBRHeader header, int sampleRate) throws AACException {
		calculateMFT(header, sampleRate);
	}

	private void calculateMFT(SBRHeader header, int sampleRate) throws AACException {
		//lower border k0
		final int sfIndex = SampleFrequency.forFrequency(sampleRate).getIndex();
		final int sfOff = MFT_SF_OFFSETS[sfIndex];
		final int k0 = MFT_START_MIN[sfIndex]+MFT_START_OFFSETS[sfOff][header.getStartFrequency(false)];
		//higher border k2
		final int stop = header.getStopFrequency(false);
		final int x;
		if(stop==15) x = 3*k0;
		else if(stop==14) x = 2*k0;
		else x = MFT_STOP_MIN[sfIndex]+MFT_STOP_OFFSETS[sfOff][header.getStopFrequency(false)-1];
		final int k2 = Math.min(64, x);

		if(k0>=k2) throw new AACException("SBR: MFT borders out of range: lower="+k0+", higher="+k2);

		//MFT calculation
		final int freqScale = header.getFrequencyScale(false);
		if(freqScale==0) calculateMFT1(header, k0, k2);
		else calculateMFT2(header, k0, k2);
	}

	//MFT calculation if frequencyScale==0
	private void calculateMFT1(SBRHeader header, int k0, int k2) {
		final int dk, bandCount;
		if(header.isAlterScale(false)) {
			dk = 2;
			bandCount = Math.round((float) (k2-k0)/4.0f)<<1;
		}
		else {
			dk = 1;
			bandCount = (int) ((float) (k2-k0)/2.0f)<<1;
		}

		final int k2Achieved = k0+bandCount*dk;
		int k2Diff = k2-k2Achieved;

		final int[] vDk = new int[bandCount];
		Arrays.fill(vDk, dk);

		if(k2Diff!=0) {
			final int incr = (k2Diff>0) ? -1 : 1;
			int k = (k2Diff>0) ? bandCount-1 : 0;
			while(k2Diff!=0) {
				vDk[k] -= incr;
				k += incr;
				k2Diff += incr;
			}
		}

		mft = new int[bandCount+1];
		mft[0] = k0;
		for(int i = 1; i<=bandCount; i++) {
			mft[i] = mft[i-1]+vDk[i-1];
		}
		System.out.println("MFT: "+Arrays.toString(mft));
	}

	//MFT calculation if frequencyScale>0
	private void calculateMFT2(SBRHeader header, int k0, int k2) {
		final int bands = MFT_INPUT1[header.getFrequencyScale(false)-1];
		final double warp = MFT_INPUT2[header.isAlterScale(false) ? 1 : 0];

		final double d = (double) k2/(double) k0;
		final boolean twoRegions;
		final int k1;
		if(d>2.2449) {
			twoRegions = true;
			k1 = 2*k0;
		}
		else {
			twoRegions = false;
			k1 = k2;
		}

		double div = (double) k1/(double) k0;
		double log = Math.log(div)*Math.log(2*LOG2);
		final int bandCount0 = 2*(int) Math.round(bands*log);

		final int[] vDk0 = new int[bandCount0];
		double pow1, pow2;
		for(int i = 0; i<bandCount0; i++) {
			pow1 = Math.pow(div, (double) (i+1)/bandCount0);
			pow2 = Math.pow(div, (double) i/bandCount0);
			vDk0[0] = (int) (Math.round(k0*pow1)-Math.round(k0*pow2));
		}
		Arrays.sort(vDk0);

		final int[] vk0 = new int[bandCount0];
		vk0[0] = k0;
		for(int i = 1; i<bandCount0; i++) {
			vk0[i] = vk0[i-1]+vDk0[i-1];
		}

		if(twoRegions) {
			div = (double) k2/(double) k0;
			log = Math.log(div);
			final int bandCount1 = 2*(int) Math.round(bands*log/(2*LOG2*warp));
			final int[] vDk1 = new int[bandCount1];
			int min = 0;
			for(int i = 0; i<bandCount1; i++) {
				pow1 = Math.pow(div, (double) (i+1)/bandCount1);
				pow2 = Math.pow(div, (double) i/bandCount1);
				vDk1[i] = (int) (k1*pow1)-(int) (k1*pow2);
				if(vDk1[i]>min) min = vDk1[i];
			}

			if(min<vDk0[vDk0.length-1]) {
				Arrays.sort(vDk1);
				int change = vDk0[vDk0.length-1]-vDk1[0];
				final int x = (int) (vDk1[bandCount1-1]-(double) vDk1[0]/2);
				if(change>x) change = x;
				vDk1[0] += change;
				vDk1[bandCount1-1] -= change;
			}

			Arrays.sort(vDk1);
			final int[] vk1 = new int[bandCount1+1];
			vk1[0] = k1;
			for(int i = 1; i<=bandCount1; i++) {
				vk1[i] = vk1[i-1]-vDk1[i-1];
			}

			mft = new int[bandCount0+bandCount1+1];
			System.arraycopy(vk0, 0, mft, 0, bandCount0+1);
			System.arraycopy(vk1, 1, mft, bandCount0+1, bandCount1);
		}
		else {
			mft = new int[bandCount0+1];
			System.arraycopy(vk0, 0, mft, 0, bandCount0+1);
		}
	}

	public int[] getMFT() {
		return mft;
	}

	public int getNMaster() {
		return mft.length-1;
	}
}
