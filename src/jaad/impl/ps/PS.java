package jaad.impl.ps;

import jaad.AACException;
import jaad.impl.BitStream;
import java.util.Arrays;

public class PS implements PSConstants, PSTables, HuffmanTables {

	//header data
	private boolean header;
	private boolean enableIID, enableICC, enableExt, enableIPDOPD;
	private int iidMode, iccMode;
	private boolean iidQuant;
	private int iidParCount, iccParCount, ipdopdParCount;
	private boolean use34;
	private int iidSteps;
	private float[] iidSf;
	//standard data
	private int frameClass;
	private int envCount;
	private final int[] borderPositions;
	//pars
	private final boolean[] iidTime, iccTime, ipdTime, opdTime;
	private final int[][] iidPars, iccPars, ipdPars, opdPars;
	//parsed data
	private int groups, hybridGroups;
	private int[] groupBorders, groupMap;
	private int parBands;
	private int decayCutoff;

	public PS() {
		iidPars = new int[MAX_ENVELOPES][MAX_IID_ICC_BANDS];
		iccPars = new int[MAX_ENVELOPES][MAX_IID_ICC_BANDS];
		ipdPars = new int[MAX_ENVELOPES][MAX_IPD_OPD_BANDS];
		opdPars = new int[MAX_ENVELOPES][MAX_IPD_OPD_BANDS];

		iidTime = new boolean[MAX_IID_ICC_BANDS];
		iccTime = new boolean[MAX_IID_ICC_BANDS];
		ipdTime = new boolean[MAX_IPD_OPD_BANDS];
		opdTime = new boolean[MAX_IPD_OPD_BANDS];

		borderPositions = new int[MAX_ENVELOPES];
	}
	//============================ decoding ==============================
	private int frame = 0;

	public int decode(BitStream in) throws AACException {
		frame++;
		final int off = in.getPosition();

		if(in.readBool()) {
			System.out.println("header:");
			header = true;
			if(enableIID = in.readBool()) {
				iidMode = in.readBits(3);
				iidQuant = iidMode>2;
				iidParCount = IID_ICC_PAR_TABLE[iidMode];
				ipdopdParCount = IPDOPD_PAR_TABLE[iidMode];
				iidSteps = iidQuant ? IID_STEPS_FINE : IID_STEPS_NORMAL;
				iidSf = iidQuant ? IID_SF_FINE : IID_SF_NORMAL;
				System.out.println("\tiid {mode="+iidMode+", quant="+iidQuant+", parsC="+iidParCount+", ipdopdParsC="+ipdopdParCount+"}");
			}
			if(enableICC = in.readBool()) {
				iccMode = in.readBits(3);
				iccParCount = IID_ICC_PAR_TABLE[iccMode];
				System.out.println("\ticc {mode="+iccMode+", parsC="+iccParCount+"}");
			}
			enableExt = in.readBool();
			if(enableExt) System.out.println("\text");
		}

		System.out.println("data:");
		frameClass = in.readBit();
		System.out.println("\tframeClass: "+frameClass);
		envCount = ENV_COUNT_TABLE[frameClass][in.readBits(2)];
		System.out.println("\tenvs: "+envCount);
		if(frameClass==1) {
			System.out.print("\tborderPositions: {");
			for(int i = 0; i<envCount; i++) {
				borderPositions[i] = in.readBits(5);
				System.out.print(borderPositions[i]);
				if(i>0) System.out.print(", ");
			}
			System.out.println("}");
		}

		if(enableIID) {
			System.out.println("\tiid data:");
			for(int i = 0; i<envCount; i++) {
				iidTime[i] = in.readBool();
				decodeIIDData(in, iidTime[i], i);
				System.out.println("\t\t"+i+":"+(iidTime[i] ? "time " : "freq ")+Arrays.toString(iidPars[i]));
			}
		}
		if(enableICC) {
			System.out.println("\ticc data:");
			for(int i = 0; i<envCount; i++) {
				iccTime[i] = in.readBool();
				decodeICCData(in, iccTime[i], i);
				System.out.println("\t\t"+i+":"+(iccTime[i] ? "time " : "freq ")+Arrays.toString(iccPars[i]));
			}
		}

		if(enableIID||enableICC) use34 = (enableIID&&iidParCount==34)||(enableICC&&iccParCount==34);
		System.out.println("\t34: "+use34);
		//set up parameters
		if(use34) {
			groupBorders = GROUP_BORDERS34;
			groupMap = GROUP_MAP34;
			groups = 32+18;
			hybridGroups = 32;
			parBands = 34;
			decayCutoff = 5;
		}
		else {
			groupBorders = GROUP_BORDERS20;
			groupMap = GROUP_MAP20;
			groups = 10+12;
			hybridGroups = 10;
			parBands = 20;
			decayCutoff = 3;
		}

		if(enableExt) {
			int size = in.readBits(4);
			if(size==15) size += in.readBits(8);

			int bitsLeft = 8*size;
			int id;
			while(bitsLeft>7) {
				id = in.readBits(2);
				bitsLeft -= 2;
				bitsLeft -= decodeExtension(in, id);
			}

			in.skipBits(bitsLeft);
		}

		final int read = in.getPosition()-off;
		return read;
	}

	private int decodeExtension(BitStream in, int id) throws AACException {
		int off = in.getPosition();
		if(id==0) {
			if(enableIPDOPD = in.readBool()) {
				System.out.println("\tipdopd data");
				for(int i = 0; i<envCount; i++) {
					ipdTime[i] = in.readBool();
					System.out.println("\t\t"+i+":"+(ipdTime[i] ? "time" : "freq"));
					decodeIPDData(in, ipdTime[i], i);
					opdTime[i] = in.readBool();
					System.out.println("\t\t"+i+":"+(opdTime[i] ? "time" : "freq"));
					decodeOPDData(in, opdTime[i], i);
				}
			}
			in.skipBit(); //reserved
		}
		return in.getPosition()-off;
	}

	private void decodeIIDData(BitStream in, boolean time, int index) throws AACException {
		final int[][] table;
		if(iidQuant) table = time ? HUFFMAN_IID_FINE_DT : HUFFMAN_IID_FINE_DF;
		else table = time ? HUFFMAN_IID_DEFAULT_DT : HUFFMAN_IID_DEFAULT_DF;
		Huffman.decode(in, table, iidPars[index], iidParCount);
	}

	private void decodeICCData(BitStream in, boolean time, int index) throws AACException {
		final int[][] table = time ? HUFFMAN_ICC_DT : HUFFMAN_ICC_DF;
		Huffman.decode(in, table, iccPars[index], iccParCount);
	}

	private void decodeIPDData(BitStream in, boolean time, int index) throws AACException {
		final int[][] table = time ? HUFFMAN_IPD_DT : HUFFMAN_IPD_DF;
		Huffman.decode(in, table, ipdPars[index], ipdopdParCount);
	}

	private void decodeOPDData(BitStream in, boolean time, int index) throws AACException {
		final int[][] table = time ? HUFFMAN_OPD_DT : HUFFMAN_OPD_DF;
		Huffman.decode(in, table, opdPars[index], ipdopdParCount);
	}

	//============================ processing ==============================
	public boolean hasHeader() {
		return header;
	}

	//left,right: [38][64][c]
	public void process(float[][][] left, float[][][] right) {
	}
}
