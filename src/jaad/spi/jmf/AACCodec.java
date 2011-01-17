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
package jaad.spi.jmf;

import jaad.AACException;
import jaad.Decoder;
import jaad.DecoderConfig;
import jaad.SampleBuffer;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.AudioFormat;

public class AACCodec implements Codec {

	private static final String NAME = "JAAD AAC Decoder";
	private static final Format[] AAC_IN = {new Format("AAC", byte[].class)};
	private static final Format[] AAC_OUT = {new AudioFormat(AudioFormat.LINEAR, AudioFormat.NOT_SPECIFIED, 16, AudioFormat.NOT_SPECIFIED, AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED)};
	private Format in, out;
	private Decoder decoder;
	private SampleBuffer sampleBuffer;

	public Format[] getSupportedInputFormats() {
		return AAC_IN;
	}

	public Format[] getSupportedOutputFormats(Format format) {
		return AAC_OUT;
	}

	public Format setInputFormat(Format format) {
		Format f = null;
		if(format.matches(AAC_IN[0])) {
			in = format;
			f = format;
		}
		return f;
	}

	public Format setOutputFormat(Format format) {
		Format f = null;
		if(format.matches(AAC_OUT[0])) {
			out = format;
			f = format;
		}
		return f;
	}

	public int process(Buffer input, Buffer output) {
		final Object o = input.getData();
		if(!(o instanceof byte[])) return BUFFER_PROCESSED_FAILED;
		byte[] data = (byte[]) o;

		try {
			decoder.decodeFrame(data, sampleBuffer);
			data = sampleBuffer.getData();
			output.setData(data);
			output.setLength(data.length);
			output.setFormat(out);
			return BUFFER_PROCESSED_OK;
		}
		catch(AACException e) {
			return BUFFER_PROCESSED_FAILED;
		}
	}

	public String getName() {
		return NAME;
	}

	public void open() throws ResourceUnavailableException {
		sampleBuffer = new SampleBuffer();
		try {
			decoder = new Decoder(new DecoderConfig());
		}
		catch(AACException e) {
			throw new ResourceUnavailableException(e.toString());
		}
	}

	public void close() {
	}

	public void reset() {
		try {
			decoder = new Decoder(new DecoderConfig());
		}
		catch(Exception e) {
		}
	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String string) {
		return null;
	}
}
