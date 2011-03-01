package tablegen;

public class PSTables {

	private static final double[] F_CENTER_20 = {
		0.5/4, 1.5/4, 2.5/4, 3.5/4,
		4.5/4*0, 5.5/4*0, -1.5/4, -0.5/4,
		3.5/2, 2.5/2, 4.5/2, 5.5/2
	};
	private static final double[] F_CENTER_34 = {
		1/12, 3/12, 5/12, 7/12,
		9/12, 11/12, 13/12, 15/12,
		17/12, -5/12, -3/12, -1/12,
		17/8, 19/8, 5/8, 7/8,
		9/8, 11/8, 13/8, 15/8,
		9/4, 11/4, 13/4, 7/4,
		17/4, 11/4, 13/4, 15/4,
		17/4, 19/4, 21/4, 15/4
	};
	private static final double[] FRAC_DELAY_Q = {
		0.43,
		0.75,
		0.347
	};

	public static void main(String[] args) {
		Utils.printTable(calcPhiFractQMF(), "PHI_FRACT_QMF");
		Utils.printTable(calcPhiFractSubQMF20(), "PHI_FRACT_SUBQMF20");
		Utils.printTable(calcPhiFractSubQMF34(), "PHI_FRACT_SUBQMF34");
		Utils.printTable(calcQFractAllpassQMF(), "Q_FRACT_ALLPASS_QMF");
		Utils.printTable(calcQFractAllpassSubQMF20(), "Q_FRACT_ALLPASS_SUBQMF20");
		Utils.printTable(calcQFractAllpassSubQMF34(), "Q_FRACT_ALLPASS_SUBQMF34");
	}

	private static float[][] calcPhiFractQMF() {
		final int len = 64;
		final float PI2 = (float) Math.PI*0.39f;
		final float add = 0.5f;

		final float[][] f = new float[len][2];
		for(int i = 0; i<len; i++) {
			f[i][0] = (float) Math.cos(PI2*(i+add));
			f[i][1] = (float) Math.sin(PI2*(i+add));
		}
		return f;
	}

	private static float[][] calcPhiFractSubQMF20() {
		final int len = F_CENTER_20.length;
		final float[][] f = new float[len][2];
		for(int i = 0; i<len; i++) {
			f[i][0] = (float) Math.cos(Math.PI*F_CENTER_20[i]*0.39);
			f[i][1] = (float) Math.sin(Math.PI*F_CENTER_20[i]*0.39);
		}
		return f;
	}

	private static float[][] calcPhiFractSubQMF34() {
		final int len = F_CENTER_34.length;
		final float[][] f = new float[len][2];
		for(int i = 0; i<len; i++) {
			f[i][0] = (float) Math.cos(Math.PI*F_CENTER_34[i]*0.39);
			f[i][1] = (float) Math.sin(Math.PI*F_CENTER_34[i]*0.39);
		}
		return f;
	}

	private static float[][][] calcQFractAllpassQMF() {
		final int len1 = 64, len2 = FRAC_DELAY_Q.length;
		final float[][][] f = new float[len1][len2][2];
		for(int i = 0; i<len1; i++) {
			for(int j = 0; j<len2; j++) {
				f[i][j][0] = (float) Math.cos(Math.PI*(i+0.5)*(FRAC_DELAY_Q[j]));
				f[i][j][1] = (float) Math.sin(Math.PI*(i+0.5)*(FRAC_DELAY_Q[j]));
			}
		}
		return f;
	}

	private static float[][][] calcQFractAllpassSubQMF20() {
		final int len1 = 12, len2 = FRAC_DELAY_Q.length;
		final float[][][] f = new float[len1][len2][2];
		for(int i = 0; i<len1; i++) {
			for(int j = 0; j<len2; j++) {
				f[i][j][0] = (float) Math.cos(Math.PI*F_CENTER_20[i]*FRAC_DELAY_Q[j]);
				f[i][j][1] = (float) Math.sin(Math.PI*F_CENTER_20[i]*FRAC_DELAY_Q[j]);
			}
		}
		return f;
	}

	private static float[][][] calcQFractAllpassSubQMF34() {
		final int len1 = 32, len2 = FRAC_DELAY_Q.length;
		final float[][][] f = new float[len1][len2][2];
		for(int j = 0; j<len1; j++) {
			for(int i = 0; i<len2; i++) {
				f[j][i][0] = (float) Math.cos(Math.PI*F_CENTER_34[j]*FRAC_DELAY_Q[i]);
				f[j][i][1] = (float) Math.sin(Math.PI*F_CENTER_34[j]*FRAC_DELAY_Q[i]);
			}
		}
		return f;
	}
}
