package tablegen;

public class PSTables {

	public static void main(String[] args) {
		Utils.printTable(generateIPDOPDQuantTable(), "IPD_OPD_QUANT");
		Utils.printTable(createFilter(FILTER_20_8, 8), "FILTER_20_8");
		Utils.printTable(createFilter(FILTER_34_12, 12), "FILTER_34_12");
		Utils.printTable(createFilter(FILTER_34_8, 8), "FILTER_34_8");
		Utils.printTable(createFilter(FILTER_34_4, 4), "FILTER_34_4");
	}

	private static float[] generateIPDOPDQuantTable() {
		final float[] f = new float[8];
		final float pi4 = (float) Math.PI/4;
		f[0] = 0;
		for(int i = 1; i<f.length; i++) {
			f[i] = f[i-1]+pi4;
		}
		return f;
	}
	//prototype filter for 8 subbands in 20-bands-mode
	private static final float[] FILTER_20_8 = {
		0.00746082949812f,
		0.02270420949825f,
		0.04546865930473f,
		0.07266113929591f,
		0.09885108575264f,
		0.11793710567217f,
		0.125f,
		0.11793710567217f,
		0.09885108575264f,
		0.07266113929591f,
		0.04546865930473f,
		0.02270420949825f,
		0.00746082949812f
	};
	//prototype filter for 2 subbands in 20-bands-mode
	private static final float[] FILTER_20_2 = {
		0f,
		0.01899487526049f,
		0f,
		-0.07293139167538f,
		0f,
		0.30596630545168f,
		0.5f,
		0.30596630545168f,
		0f,
		-0.07293139167538f,
		0f,
		0.01899487526049f,
		0f
	};
	//prototype filter for 12 subbands in 34-bands-mode
	private static final float[] FILTER_34_12 = {
		0.04081179924692f,
		0.03812810994926f,
		0.05144908135699f,
		0.06399831151592f,
		0.07428313801106f,
		0.08100347892914f,
		0.08333333333333f,
		0.08100347892914f,
		0.07428313801106f,
		0.06399831151592f,
		0.05144908135699f,
		0.03812810994926f,
		0.04081179924692f
	};
	//prototype filter for 8 subbands in 34-bands-mode
	private static final float[] FILTER_34_8 = {
		0.01565675600122f,
		0.03752716391991f,
		0.05417891378782f,
		0.08417044116767f,
		0.10307344158036f,
		0.12222452249753f,
		0.12500000000000f,
		0.12222452249753f,
		0.10307344158036f,
		0.08417044116767f,
		0.05417891378782f,
		0.03752716391991f,
		0.01565675600122f
	};
	//prototype filter for 4 subbands in 34-bands-mode
	private static final float[] FILTER_34_4 = {
		-0.05908211155639f,
		-0.04871498374946f,
		0f,
		0.07778723915851f,
		0.16486303567403f,
		0.23279856662996f,
		0.25000000000000f,
		0.23279856662996f,
		0.16486303567403f,
		0.07778723915851f,
		0f,
		-0.04871498374946f,
		-0.05908211155639f
	};

	private static float[][][] createFilter(float[] proto, int bands) {
		final float[][][] filter = new float[bands][7][2];
		int q, n;
		for(q = 0; q<bands; q++) {
			for(n = 0; n<7; n++) {
				double theta = 2*Math.PI*(q+0.5)*(n-6)/bands;
				filter[q][n][0] = proto[n]*(float) Math.cos(theta);
				filter[q][n][1] = proto[n]*(float) -Math.sin(theta);
			}
		}
		return filter;
	}
}
