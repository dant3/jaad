package tablegen;

/**
 * Generates lookup table for Intensity Stereo by the formula:
 * <code>f(x) = 0.5<sup>0.25*x</sup></code>
 * @author in-somnia
 */
public class ISScale {

	private static final int LENGTH = 255;

	public static void main(String[] args) {
		Utils.printTable(generateISScaleTable(), "is scale");
	}

	private static float[] generateISScaleTable() {
		final float[] f = new float[LENGTH];
		for(int i = 0; i<LENGTH; i++) {
			f[i] = (float) Math.pow(0.5, (0.25*i));
		}
		return f;
	}
}
