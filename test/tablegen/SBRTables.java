package tablegen;

public class SBRTables {

	private static final int[] FREQUENCIES = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000};

	public static void main(String[] args) {
		Utils.printTable(generateStartMinTable(), "MFT_START_MIN");
		final int[] stopMin = generateStopMinTable();
		Utils.printTable(stopMin, "MFT_STOP_MIN");
		Utils.printTable(generateMFTOffsetTable(), "MFT_SF_OFFSETS");
		Utils.printTable(generateStopDkTable(stopMin), "MFT_SF_STOP_OFFSETS");
	}

	//4.6.18.3.2.1
	private static int[] generateStartMinTable() {
		final int[] x = new int[FREQUENCIES.length];
		float f1, f2;
		for(int i = 0; i<x.length; i++) {
			f1 = 128.0f/(float) FREQUENCIES[i];
			if(FREQUENCIES[i]<32000) f2 = 3000.0f;
			else if(FREQUENCIES[i]>=32000&&FREQUENCIES[i]<64000) f2 = 4000.0f;
			else f2 = 5000.0f;
			f1 *= f2;
			x[i] = Math.round(f1);
		}
		return x;
	}

	private static int[] generateStopMinTable() {
		final int[] x = new int[FREQUENCIES.length];
		float f1, f2;
		for(int i = 0; i<x.length; i++) {
			f1 = 128.0f/(float) FREQUENCIES[i];
			if(FREQUENCIES[i]<32000) f2 = 6000.0f;
			else if(FREQUENCIES[i]>=32000&&FREQUENCIES[i]<64000) f2 = 8000.0f;
			else f2 = 10000.0f;
			f1 *= f2;
			x[i] = Math.round(f1);
		}
		return x;
	}

	private static int[] generateMFTOffsetTable() {
		final int[] x = new int[FREQUENCIES.length];
		for(int i = 0; i<x.length; i++) {
			if(FREQUENCIES[i]==16000) x[i] = 0;
			else if(FREQUENCIES[i]==22050) x[i] = 1;
			else if(FREQUENCIES[i]==24000) x[i] = 2;
			else if(FREQUENCIES[i]==32000) x[i] = 3;
			else if(FREQUENCIES[i]>=44100&&FREQUENCIES[i]<=64000) x[i] = 4;
			else if(FREQUENCIES[i]>64000) x[i] = 5;
			else throw new ArrayIndexOutOfBoundsException("FREQUENCY["+i+"]: "+FREQUENCIES[i]);
		}
		return x;
	}

	private static int[][] generateStopDkTable(int[] stopMin) {
		final int[][] x = new int[FREQUENCIES.length][13];
		float sm, sm2, pow1, pow2;
		int sum;
		for(int i = 0; i<x.length; i++) {
			sm = (float) stopMin[i];
			sm2 = 64.0f/sm;
			sum = 0;
			for(int j = 0; j<13; j++) {
				pow1 = (float) Math.pow(sm2, ((float) j+1.0f)/13.0f);
				pow2 = (float) Math.pow(sm2, (float) j/13.0f);
				sum += Math.round(sm*pow1)-Math.round(sm*pow2);
				x[i][j] = sum;
			}
		}
		return x;
	}
}
