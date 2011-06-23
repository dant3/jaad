package tablegen;

public class PSTables {

	public static void main(String[] args) {
		Utils.printTable(generateIPDOPDQuantTable(), "IPD_OPD_QUANT");
		Utils.printTable(createFilter(FILTER_20_8, 8), "FILTER_20_8");
		Utils.printTable(createFilter(FILTER_34_12, 12), "FILTER_34_12");
		Utils.printTable(createFilter(FILTER_34_8, 8), "FILTER_34_8");
		Utils.printTable(createFilter(FILTER_34_4, 4), "FILTER_34_4");
		
		final float[][] PHI_FRACT_20 = new float[30][2];
		final float[][][] Q_FRACT_ALLPASS_20 = new float[30][3][2];
		generateFractTables20(PHI_FRACT_20, Q_FRACT_ALLPASS_20);
		Utils.printTable(PHI_FRACT_20, "PHI_FRACT_20");
		Utils.printTable(Q_FRACT_ALLPASS_20, "Q_FRACT_ALLPASS_20");
		
		final float[][] PHI_FRACT_34 = new float[50][2];
		final float[][][] Q_FRACT_ALLPASS_34 = new float[50][3][2];
		generateFractTables34(PHI_FRACT_34, Q_FRACT_ALLPASS_34);
		Utils.printTable(PHI_FRACT_34, "PHI_FRACT_34");
		Utils.printTable(Q_FRACT_ALLPASS_34, "Q_FRACT_ALLPASS_34");
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
	private static final double Q_PHI = 0.39;
	private static final double[] QM = {0.43, 0.75, 0.347};
	//table 8.40
	private static final double[] F_CENTER_20 = {
		-3.0/8.0, -1.0/8.0, 1.0/8.0, 3.0/8.0, 5.0/8.0, 7.0/8.0, 5.0/4.0,
		7.0/4.0, 9.0/4.0, 11.0/4
	};
	//table 8.41
	private static final double[] F_CENTER_34 = {
		1.0/12.0, 3.0/12.0, 5.0/12.0, 7.0/12.0, 9.0/12.0, 11.0/12.0, 13.0/12.0,
		15.0/12.0, 17.0/12.0, -5.0/12.0, -3.0/12.0, -1.0/12.0, 17.0/8.0,
		19.0/8.0, 5.0/8.0, 7.0/8.0, 9.0/8.0, 11.0/8.0, 13.0/8.0, 15.0/8.0,
		9.0/4.0, 11.0/4.0, 13.0/4.0, 7.0/4.0, 17.0/4.0, 11.0/4.0, 13.0/4.0,
		15.0/4.0, 17.0/4.0, 19.0/4.0, 21.0/4.0, 15.0/4
	};

	private static void generateFractTables20(float[][] phiFract, float[][][] qFractAllpass) {
		double fk, tmp;
		int m;
		for(int k = 0; k<30; k++) {
			if(k<F_CENTER_20.length) fk = F_CENTER_20[k];
			else fk = k-6.5;
			tmp = -Math.PI*Q_PHI*fk;
			phiFract[k][0] = (float) Math.cos(tmp);
			phiFract[k][1] = (float) Math.sin(tmp);
			for(m = 0; m<3; m++) {
				tmp = -Math.PI*QM[m]*fk;
				qFractAllpass[k][m][0] = (float) Math.cos(tmp);
				qFractAllpass[k][m][1] = (float) Math.sin(tmp);
			}
		}
	}

	private static void generateFractTables34(float[][] phiFract, float[][][] qFractAllpass) {
		double fk, tmp;
		int m;
		for(int k = 0; k<50; k++) {
			if(k<F_CENTER_34.length) fk = F_CENTER_34[k];
			else fk = k-26.5;
			tmp = -Math.PI*Q_PHI*fk;
			phiFract[k][0] = (float) Math.cos(tmp);
			phiFract[k][1] = (float) Math.sin(tmp);
			for(m = 0; m<3; m++) {
				tmp = -Math.PI*QM[m]*fk;
				qFractAllpass[k][m][0] = (float) Math.cos(tmp);
				qFractAllpass[k][m][1] = (float) Math.sin(tmp);
			}
		}
	}
}
