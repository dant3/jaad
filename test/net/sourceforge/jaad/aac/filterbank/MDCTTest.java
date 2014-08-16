package net.sourceforge.jaad.aac.filterbank;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.TestUtils;
import org.junit.Test;

/**
 * Reference test for the IMDCT.
 * @author in-somnia
 */
public class MDCTTest {

	private final MDCT mdct256, mdct2048;

	public MDCTTest() throws AACException {
		mdct256 = new MDCT(256);
		mdct2048 = new MDCT(2048);
	}

	@Test
	public void testLong() {
		test(2048, mdct2048);
	}

	@Test
	public void testShort() {
		test(256, mdct256);
	}

	private void test(int len, MDCT mdct) {
		final float[] in = TestUtils.generateRandomVector(len/2);

		final float[] out = computeRefIMDCT(in);

		final float[] out2 = new float[len];
		mdct.process(in, 0, out2, 0);

		TestUtils.compare(out, out2);
	}

	@Test
	public void testShort2() {
		final float[] in = TestUtils.generateRandomVector(1024);
		final float[] in2 = new float[128];
		final float[] out2 = new float[256];
		float[] out;
		for(int i = 0; i<8; i++) {
			System.arraycopy(in, i*128, in2, 0, 128);
			out = computeRefIMDCT(in2);

			mdct256.process(in, i*128, out2, 0);

			TestUtils.compare(out, out2);
		}
	}

	private float[] computeRefIMDCT(float[] in) {
		final int len = in.length;
		final int n2 = len*2;
		final float[] out = new float[n2];
		final float n0 = ((float) n2*0.5f+1)*0.5f;
		final float scale = 2.0f/(float) n2;
		final float PI2 = (float) Math.PI*2.0f;

		float mult, sum, phase;
		for(int n = 0; n<n2; n++) {
			int k;
			mult = PI2*((float) n+n0)/(float) n2;
			sum = 0;

			for(k = 0; k<len; k++) {
				phase = (k+0.5f)*mult;
				sum += in[k]*Math.cos(phase);
			}

			out[n] = sum*scale;
		}
		return out;
	}
}
