package tablegen;

/**
 * Generates kaiser-bessel-derived windows for the filterbank.
 * @author in-somnia
 */
public class KBDWindow {

	private static final int ITERATIONS = 50;

	public static void main(String[] args) {
		Utils.printTable(generateKBDWindow(4.0f, 1024), "kbd window long");
		Utils.printTable(generateKBDWindow(6.0f, 128), "kbd window short");
	}

	private static float[] generateKBDWindow(float alpha, int len) {
		final float PIN = (float)Math.PI/len;
		float[] out = new float[len];
		int n, j;
		float sum = 0.0f, bessel, tmp;
		float[] f = new float[len];
		final float alpha2 = (alpha*PIN)*(alpha*PIN);

		for(n = 0; n<len; n++) {
			tmp = n*(len-n)*alpha2;
			bessel = 1.0f;
			for(j = ITERATIONS; j>0; j--) {
				bessel = bessel*tmp/(j*j)+1;
			}
			sum += bessel;
			f[n] = sum;
		}

		sum++;
		for(n = 0; n<len; n++) {
			out[n] = (float) Math.sqrt(f[n]/sum);
		}
		return out;
	}
}
