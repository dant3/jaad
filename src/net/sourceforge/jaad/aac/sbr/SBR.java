package net.sourceforge.jaad.aac.sbr;

import java.util.Arrays;
import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.SampleFrequency;
import net.sourceforge.jaad.aac.syntax.BitStream;

public class SBR implements Constants {

	private final int sampleFrequency;
	private final boolean downSampled;
	private final Header header;
	private final FrequencyTables tables;
	private ChannelData channel1, channel2;
	private boolean coupling;

	public SBR(SampleFrequency sf, boolean downSampled) {
		sampleFrequency = sf.getFrequency()*2;
		this.downSampled = downSampled;

		header = new Header();
		tables = new FrequencyTables(sampleFrequency);

		channel1 = new ChannelData(0);
		coupling = false;
	}

	public void decode(BitStream in, int count, boolean stereo, boolean crc) throws AACException {
		final int start = in.getPosition();

		if(crc) in.skipBits(10); //TODO: CRC

		if(in.readBool()) {
			header.decode(in);
			if(header.isReset()) tables.calculateTables(header);
		}

		if(header.isInitialized()) {
			if(stereo) decodeChannelPair(in);
			else decodeSingleChannel(in);

			if(in.readBool()) {
				int size = in.readBits(4);
				if(size==15) size += in.readBits(8);
				size *= 8;
				in.skipBits(size);

				/*int id;
				while(size>7) {
					id = in.readBits(2);
					size -= 2;
					decodeExtension(in, id);
				}
				in.readBits(size);*/
			}
		}

		int read = in.getPosition()-start;
		int left = count-read;
		if(left<0) throw new AACException("SBR: bitstream overread: "+Math.abs(left));
		in.skipBits(left);
	}

	private void decodeSingleChannel(BitStream in) throws AACException {
		if(in.readBool()) in.skipBits(4); //reserved

		channel1.decodeGrid(in, header);
		channel1.decodeDTDF(in);
		channel1.decodeInvF(in, tables);
		channel1.decodeEnvelope(in, tables, false);
		channel1.decodeNoise(in, tables, false);
		channel1.decodeSinusoidals(in, tables);
	}

	private void decodeChannelPair(BitStream in) throws AACException {
		if(channel2==null) channel2 = new ChannelData(1);

		if(in.readBool()) in.skipBits(8); //reserved

		coupling = in.readBool();
		if(coupling) {
			channel1.decodeGrid(in, header);
			channel2.copyGrid(channel1);
			channel1.decodeDTDF(in);
			channel2.decodeDTDF(in);
			channel1.decodeInvF(in, tables);
			channel1.decodeEnvelope(in, tables, coupling);
			channel1.decodeNoise(in, tables, coupling);
			channel2.decodeEnvelope(in, tables, coupling);
			channel2.decodeNoise(in, tables, coupling);
		}
		else {
			channel1.decodeGrid(in, header);
			channel2.decodeGrid(in, header);
			channel1.decodeDTDF(in);
			channel2.decodeDTDF(in);
			channel1.decodeInvF(in, tables);
			channel2.decodeInvF(in, tables);
			channel1.decodeEnvelope(in, tables, coupling);
			channel2.decodeEnvelope(in, tables, coupling);
			channel1.decodeNoise(in, tables, coupling);
			channel2.decodeNoise(in, tables, coupling);
		}

		channel1.decodeSinusoidals(in, tables);
		channel2.decodeSinusoidals(in, tables);
	}

	private void decodeExtension(BitStream in, int id) throws AACException {
	}

	public boolean isPSUsed() {
		return false;
	}

	public void process(float[] left, float[] right) {
	}
}
