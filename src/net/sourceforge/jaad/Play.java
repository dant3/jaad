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
package net.sourceforge.jaad;

import java.util.List;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.*;
import java.io.FileInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import net.sourceforge.jaad.mp4.boxes.Box;

/**
 * Command line example, that can decode an AAC file and play it.
 * @author in-somnia
 */
public class Play {

	private static void printTree(Box box, int off) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i<off; i++) {
			sb.append(" ");
		}
		sb.append(box.getName()+" ("+typeToString(box.getType())+")");
		System.out.println(sb.toString());
		for(Box b : box.getChildren()) {
			printTree(b, off+1);
		}
	}

	private static String typeToString(long l) {
		byte[] b = new byte[4];
		b[0] = (byte) ((l>>24)&0xFF);
		b[1] = (byte) ((l>>16)&0xFF);
		b[2] = (byte) ((l>>8)&0xFF);
		b[3] = (byte) (l&0xFF);
		return new String(b);
	}
	private static final String USAGE = "usage:\njaad.Play [-mp4] <infile>\n\n\t-mp4\tinput file is in MP4 container format";

	public static void main(String[] args) {
		try {
			if(args.length<1) printUsage();
			if(args[0].equals("-mp4")) {
				if(args.length<2) printUsage();
				else decodeMP4(args[1]);
			}
			else decodeAAC(args[0]);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("error while decoding: "+e.toString());
		}
	}

	private static void printUsage() {
		System.out.println(USAGE);
		System.exit(1);
	}

	private static void decodeMP4(String in) throws Exception {
		SourceDataLine line = null;
		byte[] b;
		try {
			final MP4Container cont = new MP4Container(new FileInputStream(in));
			List<Box> boxes = cont.getBoxes();
			for(Box box : boxes) {
				//printTree(box, 0);
			}
			final Movie movie = cont.getMovie();
			final AudioTrack track = (AudioTrack) movie.getTracks(Type.AUDIO).get(0);
			final AudioFormat aufmt = new AudioFormat(track.getSampleRate(), track.getSampleSize(), track.getChannelCount(), true, true);
			line = AudioSystem.getSourceDataLine(aufmt);
			line.open();
			line.start();

			final Decoder dec = new Decoder(track.getDecoderSpecificInfo());

			Frame frame;
			final SampleBuffer buf = new SampleBuffer();
			while(track.hasMoreFrames()) {
				frame = track.readNextFrame();
				dec.decodeFrame(frame.getData(), buf);
				b = buf.getData();
				line.write(b, 0, b.length);
			}
		}
		finally {
			if(line!=null) {
				line.stop();
				line.close();
			}
		}
	}

	private static void decodeAAC(String in) throws Exception {
		SourceDataLine line = null;
		byte[] b;
		try {
			final Decoder dec = new Decoder(new FileInputStream(in));
			final SampleBuffer buf = new SampleBuffer();
			while(true) {
				if(!dec.decodeFrame(buf)) break;

				if(line==null) {
					final AudioFormat aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
					line = AudioSystem.getSourceDataLine(aufmt);
					line.open();
					line.start();
				}
				b = buf.getData();
				line.write(b, 0, b.length);
			}
		}
		finally {
			if(line!=null) {
				line.stop();
				line.close();
			}
		}
	}
}
