package net.sourceforge.jaad.aac.sbr;

import net.sourceforge.jaad.aac.TestUtils;
import org.junit.Test;

public class FilterbankTest implements FilterbankTables {

	private static final int[] BIT_REVERSE = {0, 16, 24, 8, 28, 12, 20, 4, 30, 14, 22, 6, 26, 10, 18,
		2, 31, 15, 23, 7, 27, 11, 19, 3, 29, 13, 21, 5, 25, 9, 17, 1};

	@Test
	public void testDST() {
		final float[] in = TestUtils.generateRandomVector(32);
		final float[] ref = computeRefDST(in);

		//new QMFSynthesis(new Filterbank(), 32).computeDST(in); //for testing: remove private modifier from method

		TestUtils.compare(ref, in);
	}

	private float[] computeRefDST(float[] in) {
		final int len = in.length;
		final float PIN = (float) (Math.PI/(float) len);
		final float[] out = new float[len];

		for(int k = 0; k<len; k++) {
			out[k] = 0;
			for(int n = 0; n<len; n++) {
				out[k] += in[n]*Math.sin(PIN*(n+0.5)*(k+0.5));
			}
		}

		return out;
	}

	@Test
	public void testDCT() {
		final float[] in = TestUtils.generateRandomVector(32);
		final float[] ref = computeRefDCT(in);

		//new QMFSynthesis(new Filterbank(), 32).computeDCT(in); //for testing: remove private modifier from method

		TestUtils.compare(ref, in);
	}

	//DCT-IV
	private float[] computeRefDCT(float[] in) {
		final int len = in.length;
		final float PIN = (float) (Math.PI/(float) len);
		final float[] out = new float[len];

		for(int k = 0; k<len; k++) {
			out[k] = 0;
			for(int n = 0; n<len; n++) {
				out[k] += in[n]*Math.cos(PIN*(n+0.5)*(k+0.5));
			}
		}

		return out;
	}

	@Test
	public void testFFT() {
		final float[][] in = TestUtils.generateRandomVectorC(32);
		final float[][] ref = computeRefDFT(in);

		//Filterbank.computeFFT(in); //for testing: remove private modifier from FFT
		float[][] out = new float[32][2];
		for(int i = 0; i<32; i++) {
			out[BIT_REVERSE[i]] = in[i];
		}

		TestUtils.compare(ref, out);
	}

	private float[][] computeRefDFT(float[][] in) {
		final int len = in.length;
		final float[][] out = new float[len][2];
		final float PI2 = (float) Math.PI*2.0f;

		float real, imag, phase, sin, cos;
		for(int k = 0; k<len; k++) {
			real = 0;
			imag = 0;
			for(int j = 0; j<len; j++) {
				phase = PI2*((k*j)/(float) len);
				sin = (float) Math.sin(phase);
				cos = (float) Math.cos(phase);
				real += (cos*in[j][0])-(sin*in[j][1]);
				imag += (cos*in[j][1])+(sin*in[j][0]);
			}
			out[k][0] = real;
			out[k][1] = imag;
		}

		return out;
	}
}
