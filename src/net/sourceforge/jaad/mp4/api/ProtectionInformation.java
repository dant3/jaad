package net.sourceforge.jaad.mp4.api;

import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxFactory;
import net.sourceforge.jaad.mp4.boxes.BoxTypes;
import net.sourceforge.jaad.mp4.boxes.impl.OriginalFormatBox;

/**
 * This class contains information about a DRM system.
 */
public class ProtectionInformation {

	private final OriginalFormatBox frma;

	ProtectionInformation(Box sinf) {
		frma = (OriginalFormatBox) sinf.getChild(BoxTypes.ORIGINAL_FORMAT_BOX);
	}

	public String getOriginalFormat() {
		return BoxFactory.typeToString(frma.getOriginalFormat());
	}
}
