package net.sourceforge.jaad.aac.sbr2;

interface SBRConstants {

	//frame classes
	int FIXFIX = 0;
	int FIXVAR = 1;
	int VARFIX = 2;
	int VARVAR = 3;
	//indizes for frequency tables
	int HIGH = 1;
	int LOW = 0;
	int RATE = 2;
	int TIME_SLOTS = 16; //TODO: 15 for 960-sample frames
	int NOISE_FLOOR_OFFSET = 6;
	//extension ids
	int EXTENSION_ID_PS = 2;
	//helper constants
	double LOG2 = 0.6931471805599453;
	int[] PAN_OFFSETS = {24, 12};
}
