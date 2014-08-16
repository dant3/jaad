package tablegen;

/**
 * Generates lookup tables for standard inverse quantization by the formulas:
 * <code>f(x) = x<sup>4/3</sup></code>
 * and
 * <code>f(x) = 2<sup>0.25*(x-100)</sup></code>
 * @author in-somnia
 */
public class InvQuantTables {

	private static final int LENGTH = 8191;
	private static final float FOUR_THIRD = 4.0f/3.0f;

	public static void main(String[] args) {
		Utils.printTable(generateIQTable(), "iq table");
		Utils.printTable(generateSFTable(), "scalefactor table");
	}

	private static float[] generateIQTable() {
		float[] f = new float[LENGTH];
		for(int i = 0; i<f.length; i++) {
			f[i] = (float) Math.pow(i, FOUR_THIRD);
		}
		return f;
	}

	private static float[] generateSFTable() {
		float[] f = new float[428];
		for(int i = 0; i<428; i++) {
			f[i] = (float) Math.pow(2, (i-200)/4.0);
		}
		return f;
	}
}
