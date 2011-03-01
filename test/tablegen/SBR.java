package tablegen;

public class SBR {

	private static final int FFT_TABLE_LEN = 32;
	private static final float PI2 = (float) Math.PI*2;

	public static void main(String[] args) {
		Utils.printTable(generateCeilLogTable(), "ceil log table");
		Utils.printTable(generateFFTTable(), "fft table");
	}

	private static int[] generateCeilLogTable() {
		final int[] x = new int[6];
		for(int i = 0; i<x.length; i++) {
			x[i] = (int) Math.ceil(Math.log(i+1)/Math.log(2));
		}
		return x;
	}

	private static float[][] generateFFTTable() {
		final float[][] x = new float[FFT_TABLE_LEN][2];
		float d;
		for(int i = 0; i<FFT_TABLE_LEN/2; i++) {
			d = (float) i/(float) FFT_TABLE_LEN;
			x[i][0] = (float) Math.cos(PI2*d);
			x[i][1] = (float) Math.sin(-PI2*d);
		}
		return x;
	}
}
