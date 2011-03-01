package tablegen;

public class GCFilterBank {

	public static void main(String[] args) {
		Utils.printTable(generateFFTTable(128), "fft table 128");
		Utils.printTable(generateFFTTable(16), "fft table 16");
		Utils.printTable(generateIMDCTTable(256), "imdct table 256");
		Utils.printTable(generateIMDCTTable(32), "imdct table ");
		Utils.printTable(generateIMDCTPostTable(256), "imdct post table 256");
		Utils.printTable(generateIMDCTPostTable(32), "imdct post table 32");
	}

	private static float[][] generateFFTTable(int len) {
		float[][] f = new float[len/2][2];
		for(int i = 0; i<len/2; ++i) {
			f[i][0] = (float) Math.cos(Math.PI*2*i/len);
			f[i][1] = (float) -Math.sin(Math.PI*2*i/len);
		}
		return f;
	}

	private static float[][] generateIMDCTTable(int len) {
		final float[][] f = new float[len/2][2];
		float phase;
		for(int i = 0; i<len/2; i++) {
			phase = -(float) Math.PI*2.0f*i/len;
			f[i][0] = (float) Math.cos(phase);
			f[i][1] = (float) Math.sin(phase);
		}
		return f;
	}

	private static float[][] generateIMDCTPostTable(int len) {
		float[][] f = new float[len/2][4];
		float[] tmp1 = new float[2], tmp2 = new float[2];
		float w;
		for(int i = 0; i<len/2; i++) {
			w = (float) Math.PI*2.0f*(2.0f*i+1)/(8*len);
			tmp1[0] = (float) Math.cos(w);
			tmp1[1] = (float) Math.sin(w);
			tmp2[0] = (float) Math.cos(5.0f*w);
			tmp2[1] = (float) Math.sin(5.0f*w);
			f[i][0] = (tmp1[0]-tmp2[1])/2.0f;
			f[i][1] = (tmp1[0]+tmp2[1])/2.0f;
			f[i][2] = (tmp1[1]+tmp2[0])/2.0f;
			f[i][3] = (-tmp1[1]+tmp2[0])/2.0f;
		}
		return f;
	}
}
