/*
 *  Copyright (C) 2011 in-somnia
 * 
 *  This file is part of JAAD.
 * 
 *  JAAD is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  JAAD is distributed in the hope that it will be useful, but WITHOUT 
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General 
 *  Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

/**
 * Command line example, that can decode an AAC stream from an Shoutcast/Icecast
 * server.
 * 
 * @author in-somnia
 */
public class Radio {

	private static final String USAGE = "usage:\nnet.sourceforge.jaad.Radio <url>";

	public static void main(String[] args) {
		try {
			if(args.length<1) printUsage();
			else decode(args[0]);
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

	private static void decode(String arg) throws Exception {
		final SampleBuffer buf = new SampleBuffer();

		SourceDataLine line = null;
		byte[] b;
		try {
			final URL url = new URL(arg);
			final InputStream in = url.openStream();
			//skip icy header
			final BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String x;
			do {
				x = br.readLine();
			}
			while(x!=null&&!x.trim().equals(""));

			final ADTSDemultiplexer adts = new ADTSDemultiplexer(in);
			AudioFormat aufmt = new AudioFormat(adts.getSampleFrequency(), 16, adts.getChannelCount(), true, true);
			final Decoder dec = new Decoder(adts.getDecoderSpecificInfo());

			while(true) {
				b = adts.readNextFrame();
				dec.decodeFrame(b, buf);

				if(line!=null&&!line.getFormat().matches(aufmt)) {
					//format has changed (e.g. SBR has started)
					line.stop();
					line.close();
					line = null;
					aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
				}
				if(line==null) {
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
