package net.sourceforge.jaad.aac.ps2;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;

class PSHeader implements PSTables {

	//iid
	private boolean iidEnabled;
	private int iidMode, iidPars;
	private boolean iidQuant;
	//icc
	private boolean iccEnabled;
	private int iccMode, iccPars;
	private boolean iccMixingB;
	//ipd/opd
	private boolean ipdopdEnabled;
	private int ipdopdPars;
	//ext
	private boolean extEnabled;

	PSHeader() {
		iidEnabled = false;
		iidMode = 0;
		iidPars = 0;
		iccEnabled = false;
		iccMode = 0;
		iccPars = 0;
		ipdopdEnabled = false;
		ipdopdPars = 0;
		extEnabled = false;
	}

	void decode(BitStream in) throws AACException {
		if(iidEnabled = in.readBool()) {
			iidMode = in.readBits(3);
			iidPars = IID_PARS[iidMode%3];
			iidQuant = iidMode>2;

			ipdopdPars = IPD_OPD_PARS[iidMode%3];
		}
		else {
			//TODO: are these default values correct?
			iidMode = 0;
			iidPars = 0;
			iidQuant = false;
			ipdopdPars = 0;
		}

		if(iccEnabled = in.readBool()) {
			iccMode = in.readBits(3);
			iccPars = IID_PARS[iccMode]; //same as for iid
			iccMixingB = iccMode>2;
		}
		else {
			//TODO: are these default values correct?
			iccMode = 1;
			iccPars = 1;
			iccMixingB = false;
		}

		extEnabled = in.readBool();
	}

	public boolean isIIDEnabled() {
		return iidEnabled;
	}

	public int getIIDMode() {
		return iidMode;
	}

	public int getIIDPars() {
		return iidPars;
	}

	public boolean useIIDQuantFine() {
		return iidQuant;
	}

	public boolean isIPDOPDEnabled() {
		return ipdopdEnabled;
	}

	void setIPDOPDEnabled(boolean enabled) {
		ipdopdEnabled = enabled;
	}

	public int getIPDOPDPars() {
		return ipdopdPars;
	}

	public boolean isICCEnabled() {
		return iccEnabled;
	}

	public int getICCMode() {
		return iccMode;
	}

	public int getICCPars() {
		return iccPars;
	}

	public boolean useICCMixingB() {
		return iccMixingB;
	}

	public boolean isExtEnabled() {
		return extEnabled;
	}
}
