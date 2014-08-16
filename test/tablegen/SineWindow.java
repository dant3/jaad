package tablegen;

/**
 * Generates sine windows for the filterbank.
 * @author in-somnia
 */
public class SineWindow {

	public static void main(String[] args) {
		Utils.printTable(generateSineWindow(1024), "sine window long");
		Utils.printTable(generateSineWindow(128), "sine window short");
	}

	private static float[] generateSineWindow(int len) {
		float[] d = new float[len];
		for(int i = 0; i<len; i++) {
			d[i] = (float) Math.sin((i+0.5)*(Math.PI/(2.0*len)));
		}
		return d;
	}
}
