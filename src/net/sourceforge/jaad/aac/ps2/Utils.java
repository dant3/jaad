package net.sourceforge.jaad.aac.ps2;

class Utils {

	public static void map10To34(int[] par, boolean full) {
		if(full) {
			par[33] = par[9];
			par[32] = par[9];
			par[31] = par[9];
			par[30] = par[9];
			par[29] = par[9];
			par[28] = par[9];
			par[27] = par[8];
			par[26] = par[8];
			par[25] = par[8];
			par[24] = par[8];
			par[23] = par[7];
			par[22] = par[7];
			par[21] = par[7];
			par[20] = par[7];
			par[19] = par[6];
			par[18] = par[6];
			par[17] = par[5];
			par[16] = par[5];
		}
		par[15] = par[4];
		par[14] = par[4];
		par[13] = par[4];
		par[12] = par[4];
		par[11] = par[3];
		par[10] = par[3];
		par[9] = par[2];
		par[8] = par[2];
		par[7] = par[2];
		par[6] = par[2];
		par[5] = par[1];
		par[4] = par[1];
		par[3] = par[1];
		par[2] = par[0];
		par[1] = par[0];
		par[0] = par[0];
	}

	public static void map20To34(int[] par, boolean full) {
		if(full) {
			par[33] = par[19];
			par[32] = par[19];
			par[31] = par[18];
			par[30] = par[18];
			par[29] = par[18];
			par[28] = par[18];
			par[27] = par[17];
			par[26] = par[17];
			par[25] = par[16];
			par[24] = par[16];
			par[23] = par[15];
			par[22] = par[15];
			par[21] = par[14];
			par[20] = par[14];
			par[19] = par[13];
			par[18] = par[12];
			par[17] = par[11];
		}
		par[16] = par[10];
		par[15] = par[9];
		par[14] = par[9];
		par[13] = par[8];
		par[12] = par[8];
		par[11] = par[7];
		par[10] = par[6];
		par[9] = par[5];
		par[8] = par[5];
		par[7] = par[4];
		par[6] = par[4];
		par[5] = par[3];
		par[4] = (par[2]+par[3])/2;
		par[3] = par[2];
		par[2] = par[1];
		par[1] = (par[0]+par[1])/2;
		par[0] = par[0];
	}

	public static void map10To20(int[] par, boolean full) {
		final int[] tmp = new int[par.length];
		int i;
		if(full) i = 9;
		else {
			i = 4;
			par[10] = 0;
		}
		for(; i>=0; i--) {
			par[2*i+1] = par[i];
			par[2*i] = par[i];
		}
		System.arraycopy(tmp, 0, par, 0, i);
	}

	public static void map34To20(int[] par, boolean full) {
		par[0] = (2*par[0]+par[1])/3;
		par[1] = (par[1]+2*par[2])/3;
		par[2] = (2*par[3]+par[4])/3;
		par[3] = (par[4]+2*par[5])/3;
		par[4] = (par[6]+par[7])/2;
		par[5] = (par[8]+par[9])/2;
		par[6] = par[10];
		par[7] = par[11];
		par[8] = (par[12]+par[13])/2;
		par[9] = (par[14]+par[15])/2;
		par[10] = par[16];
		if(full) {
			par[11] = par[17];
			par[12] = par[18];
			par[13] = par[19];
			par[14] = (par[20]+par[21])/2;
			par[15] = (par[22]+par[23])/2;
			par[16] = (par[24]+par[25])/2;
			par[17] = (par[26]+par[27])/2;
			par[18] = (par[28]+par[29]+par[30]+par[31])/4;
			par[19] = (par[32]+par[33])/2;
		}
	}

	public static void map20To34(float[] par) {
		par[33] = par[19];
		par[32] = par[19];
		par[31] = par[18];
		par[30] = par[18];
		par[29] = par[18];
		par[28] = par[18];
		par[27] = par[17];
		par[26] = par[17];
		par[25] = par[16];
		par[24] = par[16];
		par[23] = par[15];
		par[22] = par[15];
		par[21] = par[14];
		par[20] = par[14];
		par[19] = par[13];
		par[18] = par[12];
		par[17] = par[11];
		par[16] = par[10];
		par[15] = par[9];
		par[14] = par[9];
		par[13] = par[8];
		par[12] = par[8];
		par[11] = par[7];
		par[10] = par[6];
		par[9] = par[5];
		par[8] = par[5];
		par[7] = par[4];
		par[6] = par[4];
		par[5] = par[3];
		par[4] = (par[2]+par[3])*0.5f;
		par[3] = par[2];
		par[2] = par[1];
		par[1] = (par[0]+par[1])*0.5f;
		par[0] = par[0];
	}

	public static void map34To20(float[] par) {
		par[0] = (2*par[0]+par[1])*0.33333333f;
		par[1] = (par[1]+2*par[2])*0.33333333f;
		par[2] = (2*par[3]+par[4])*0.33333333f;
		par[3] = (par[4]+2*par[5])*0.33333333f;
		par[4] = (par[6]+par[7])*0.5f;
		par[5] = (par[8]+par[9])*0.5f;
		par[6] = par[10];
		par[7] = par[11];
		par[8] = (par[12]+par[13])*0.5f;
		par[9] = (par[14]+par[15])*0.5f;
		par[10] = par[16];
		par[11] = par[17];
		par[12] = par[18];
		par[13] = par[19];
		par[14] = (par[20]+par[21])*0.5f;
		par[15] = (par[22]+par[23])*0.5f;
		par[16] = (par[24]+par[25])*0.5f;
		par[17] = (par[26]+par[27])*0.5f;
		par[18] = (par[28]+par[29]+par[30]+par[31])*0.25f;
		par[19] = (par[32]+par[33])*0.5f;
	}
}
