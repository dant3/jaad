package net.sourceforge.jaad.aac.filterbank;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.TestUtils;
import org.junit.Test;

/**
 * Reference test for the IFFT.
 * @author in-somnia
 */
public class FFTTest {

	private static final int LONG_LEN = 512,SHORT_LEN = 64;
	@Test
	public void testLong() throws AACException {
		test(LONG_LEN, new FFT(LONG_LEN));
	}

	@Test
	public void testShort() throws AACException {
		test(SHORT_LEN, new FFT(SHORT_LEN));
	}

	private void test(int len, FFT fft) {
		final float[][] in = TestUtils.generateRandomVectorC(len);
		final float[][] ref = computeRefDFT(in);

		final float[][] out = new float[len][2];
		for(int i = 0; i<len; i++) {
			out[i][0] = in[i][0];
			out[i][1] = in[i][1];
		}
		fft.process(out, false);

		TestUtils.compare(ref, out);
	}

	@Test
	public void testFW() throws AACException {
		FFT fft = new FFT(LONG_LEN);

		final float[][] in = TestUtils.generateRandomVectorC(LONG_LEN);
		final float[][] in2 = new float[LONG_LEN][2];
		for(int i = 0; i<LONG_LEN; i++) {
			in2[i][0] = in[i][0];
			in2[i][1] = in[i][1];
		}

		fft.process(in2, true);
		//forward FFT is unscaled
		for(int i = 0; i<LONG_LEN; i++) {
			in2[i][0] /= LONG_LEN;
			in2[i][1] /= LONG_LEN;
		}

		fft.process(in2, false);
		TestUtils.compare(in, in2);
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
