package net.sourceforge.jaad.aac.sbr2;

import net.sourceforge.jaad.aac.sbr2.AnalysisFilterbank;
import net.sourceforge.jaad.aac.sbr2.FilterbankTables;
import net.sourceforge.jaad.aac.sbr2.SynthesisFilterbank;
import net.sourceforge.jaad.aac.TestUtils;
import org.junit.Test;

public class FilterbankTest implements FilterbankTables {

	private static final int TEST_RUNS = 20;
	private final float[] x = new float[320];

	@Test
	public void testAnalysis() {
		AnalysisFilterbank af = new AnalysisFilterbank();

		for(int run = 0; run<TEST_RUNS; run++) {
			final float[] in = TestUtils.generateRandomVector(1024);

			final float[][][] out1 = new float[32][32][2];
			af.process(in, out1, 0);

			final float[][][] out2 = new float[32][32][2];
			processReferenceAnalysis(in, out2);

			int j;
			for(int i = 0; i<32; i++) {
				TestUtils.compare(out2[i], out1[i]);
			}
		}
	}

	private void processReferenceAnalysis(float[] in, float[][][] out) {
		final float[] z = new float[320];
		final float[] u = new float[64];

		int n, j, k;
		int off = 0;

		for(int l = 0; l<32; l++) {
			for(n = 319; n>=32; n--) {
				x[n] = x[n-32];
			}

			for(n = 31; n>=0; n--) {
				x[n] = in[off];
				off++;
			}

			for(n = 0; n<=319; n++) {
				z[n] = x[n]*(float) WINDOW[2*n];
			}

			for(n = 0; n<=63; n++) {
				u[n] = z[n];
				for(j = 1; j<=4; j++) {
					u[n] = u[n]+z[n+j*64];
				}
			}

			for(k = 0; k<=31; k++) {
				out[k][l][0] = u[0]*2*(float) Math.cos(Math.PI/64*(k+0.5)*(-0.5));
				out[k][l][1] = u[0]*2*(float) Math.sin(Math.PI/64*(k+0.5)*(-0.5));
				for(n = 1; n<=63; n++) {
					out[k][l][0] += u[n]*2*(float) Math.cos(Math.PI/64*(k+0.5)*(2*n-0.5));
					out[k][l][1] += u[n]*2*(float) Math.sin(Math.PI/64*(k+0.5)*(2*n-0.5));
				}
			}
		}
	}
	private final float[] v = new float[1280];

	@Test
	public void testSynthesis() {
		SynthesisFilterbank sf = new SynthesisFilterbank();

		for(int run = 0; run<TEST_RUNS; run++) {
			final float[][][] in = new float[64][][];
			for(int i = 0; i<64; i++) {
				in[i] = TestUtils.generateRandomVectorC(32);
			}

			final float[] out1 = new float[2048];
			sf.process(in, out1, 0);

			final float[] out2 = new float[2048];
			processReferenceSynthesis(in, out2);

			TestUtils.compare(out2, out1);
		}
	}

	private void processReferenceSynthesis(float[][][] in, float[] out) {
		final float[] g = new float[640];
		final float[] w = new float[640];
		float re, im, re1, im1, temp;
		int n, k;
		int off = 0;

		for(int l = 0; l<32; l++) {
			for(n = 1279; n>=128; n--) {
				v[n] = v[n-128];
			}

			for(n = 0; n<=127; n++) {
				v[n] = 0;
				for(k = 0; k<=63; k++) {
					re = in[k][l][0];
					im = in[k][l][1];
					re1 = (float) Math.cos(Math.PI/128.0*(k+0.5)*(2*n-255))*(1.0f/64.0f);
					im1 = (float) Math.sin(Math.PI/128.0*(k+0.5)*(2*n-255))*(1.0f/64.0f);
					v[n] += (re*re1)-(im*im1);
				}
			}

			for(n = 0; n<=4; n++) {
				for(k = 0; k<=63; k++) {
					g[128*n+k] = v[256*n+k];
					g[128*n+64+k] = v[256*n+192+k];
				}
			}

			for(n = 0; n<=639; n++) {
				w[n] = g[n]*(float) WINDOW[n];
			}

			for(k = 0; k<=63; k++) {
				temp = w[k];
				for(n = 1; n<=9; n++) {
					temp = temp+w[64*n+k];
				}
				out[off] = temp;
				off++;
			}
		}
	}
}
