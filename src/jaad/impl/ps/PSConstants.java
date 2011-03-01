package jaad.impl.ps;

interface PSConstants {

	int MAX_ENVELOPES = 5;
	int MAX_IID_ICC_BANDS = 34;
	int MAX_IPD_OPD_BANDS = 17;
	int ALLPASS_LINKS = 3;
	//decorrelation
	float ALPHA_DECAY = 0.76592833836465f;
	float ALPHA_SMOOTH = 0.25f;
	float DECAY_SLOPE = 0.05f;
	float ALLPASS_BANDS = 22;
	//stereo processing
	int IID_STEPS_NORMAL = 7;
	int IID_STEPS_FINE = 15;
	float SQRT2 = 1.4142135623731f;
}
