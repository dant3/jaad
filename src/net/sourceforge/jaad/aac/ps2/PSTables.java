package net.sourceforge.jaad.aac.ps2;

interface PSTables {

	int[] IID_PARS = {10, 20, 34};
	int[] IPD_OPD_PARS = {5, 11, 17};
	//iid default quantization factors: index -7...+7
	int[] IID_QUANT_DEFAULT = {
		-25, -18, -14, -10, -7, -4, -2, 0, 2, 4, 7, 10, 14, 18, 25};
	//iid fine quantization factors: index -15...+15
	int[] IID_QUANT_FINE = {
		-50, -45, -40, -35, -30, -25, -22, -19, -16, -13, -10, -8, -6, -4, -2, 0,
		2, 4, 6, 8, 10, 13, 16, 19, 22, 25, 30, 35, 40, 45, 50
	};
	//icc quantization factors: index 0...7
	float[] ICC_QUANT = {
		1f, 0.937f, 0.84118f, 0.60092f, 0.36764f, 0f, -0.589f, -1f
	};
	//ipd/opd quantization factors: index 0...7
	float[] IPD_OPD_QUANT = {0.0f, 0.7853982f, 1.5707964f, 2.3561945f,
		3.1415927f, 3.926991f, 4.712389f, 5.497787f};
}
