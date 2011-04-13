package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.boxes.impl.od.ObjectDescriptor;

public class IPMPControlBox extends FullBox {

	private ObjectDescriptor toolList;
	private ObjectDescriptor[] ipmpDescriptors;

	public IPMPControlBox() {
		super("IPMP Control Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		super.decode(in);

		toolList = ObjectDescriptor.createDescriptor(in);
		left -= toolList.getBytesRead();

		final int count = in.read();
		left--;

		ipmpDescriptors = new ObjectDescriptor[count];
		for(int i = 0; i<count; i++) {
			ipmpDescriptors[i] = ObjectDescriptor.createDescriptor(in);
			left -= ipmpDescriptors[i].getBytesRead();
		}
	}

	public ObjectDescriptor getToolList() {
		return toolList;
	}

	public ObjectDescriptor[] getIPMPDescriptors() {
		return ipmpDescriptors;
	}
}
