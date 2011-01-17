package jaad.impl.ps;

interface PSConstants {

	int MAX_PS_ENVELOPES = 5;
	int NO_ALLPASS_LINKS = 3;
	int SHORT_DELAY_BAND = 35;
	int NR_ALLPASS_BANDS = 22;
	float ALPHA_DECAY = 0.76592833836465f;
	float ALPHA_SMOOTH = 0.25f;
	float DECAY_SLOPE = 0.05f;
	float SQRT2 = 1.414213562f;
	int RATE = 2;
	int TIME_SLOTS = 16;
	int TIME_SLOTS_RATE = RATE*TIME_SLOTS;
	int EXTENSION_ID_IPDOPD = 0;
	float REDUCTION_RATIO_GAMMA = 1.5f;
	int IID_STEPS_LONG = 15;
	int IID_STEPS_SHORT = 7;
}
