/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jaad.util.mp4.boxes;

import jaad.util.mp4.BoxFactory;
import jaad.util.mp4.ContainerBox;
import jaad.util.mp4.ContainerBoxImpl;
import jaad.util.mp4.MP4InputStream;
import java.io.IOException;

public class SampleTableBox extends ContainerBoxImpl {

	private boolean sound = false;

	@Override
	public void decode(MP4InputStream in) throws IOException {
		sound = ((ContainerBox) parent).containsChild(BoxFactory.SOUND_MEDIA_HEADER_BOX);
		//skip the contents if it contains video or meta data
		if(sound) super.decode(in);
	}

	public boolean isSound() {
		return sound;
	}
}
