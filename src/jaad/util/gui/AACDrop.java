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
package jaad.util.gui;

import jaad.Decoder;
import jaad.DecoderConfig;
import jaad.SampleBuffer;
import jaad.util.mp4.AudioFrame;
import jaad.util.mp4.MP4Reader;
import jaad.util.wav.WaveFileWriter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

/**
 * Small GUI to decode AAC files.
 * @author in-somnia
 */
public class AACDrop extends JFrame {

	private class FileTransferHandler extends TransferHandler {

		private File out;
		private WaveFileWriter wav = null;

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transfer) {
			if(transfer.length>1) return false;
			return transfer[0].equals(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			boolean ret = false;
			label.setText(DECODING);
			final DataFlavor flavor = t.getTransferDataFlavors()[0];
			try {
				if(flavor.equals(DataFlavor.javaFileListFlavor)) {
					final File file = (File) ((List) t.getTransferData(DataFlavor.javaFileListFlavor)).get(0);
					out = getOutputFile(file);

					final PushbackInputStream in = new PushbackInputStream(new FileInputStream(file), 8);
					final byte[] b = new byte[8];
					in.read(b);
					in.unread(b);

					if(new String(b).substring(4).equals("ftyp")) decodeMP4(in);
					else decodeAAC(in);
					ret = true;
				}
			}
			catch(IOException e) {
				reportError(e);
			}
			catch(UnsupportedFlavorException e) {
				reportError(e);
			}
			label.setText(WAITING);
			return ret;
		}

		private File getOutputFile(File file) {
			final String name = file.getAbsolutePath();
			final int i = name.lastIndexOf('.');
			String s;
			if(i>0) s = name.substring(0, i)+".wav";
			else s = name+".wav";
			return new File(s);
		}

		private void decodeMP4(PushbackInputStream in) throws IOException {
			try {
				final MP4Reader mp4 = new MP4Reader(in);
				final DecoderConfig conf = DecoderConfig.parseMP4DecoderSpecificInfo(mp4.getDecoderSpecificInfo());
				final Decoder dec = new Decoder(conf);

				AudioFrame frame;
				final SampleBuffer buf = new SampleBuffer();
				while(mp4.hasMoreFrames()) {
					frame = mp4.readNextFrame();
					dec.decodeFrame(frame.getData(), buf);

					if(wav==null) {
						final SampleBuffer.Format format = buf.getFormat();
						wav = new WaveFileWriter(out, format.getSampleRate(), format.getChannels(), format.getBitsPerSample());
					}
					wav.write(buf.getData());
				}
			}
			finally {
				wav.close();
			}
		}

		private void decodeAAC(PushbackInputStream in) throws IOException {
			try {
				final DecoderConfig conf = DecoderConfig.parseTransportHeader(in);
				final Decoder dec = new Decoder(conf);

				final SampleBuffer buf = new SampleBuffer();
				while(true) {
					if(!dec.decodeFrame(buf)) break;

					if(wav==null) {
						final SampleBuffer.Format format = buf.getFormat();
						wav = new WaveFileWriter(out, format.getSampleRate(), format.getChannels(), format.getBitsPerSample());
					}
					wav.write(buf.getData());
				}
			}
			finally {
				wav.close();
			}
		}

		private void reportError(Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	private static final String WAITING = "drop AAC/MP4 file here...";
	private static final String DECODING = "decoding...";
	private final JLabel label;

	public AACDrop() {
		super("AACDrop");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JComponent c = (JComponent) getContentPane();
		c.setLayout(new BorderLayout());
		c.add("Center", label = new JLabel(WAITING));
		c.setTransferHandler(new FileTransferHandler());
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
		}
		final AACDrop drop = new AACDrop();
		drop.setSize(150, 100);
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		drop.setLocation(dim.width/2-drop.getWidth()/2, dim.height/2-drop.getHeight()/2);
		drop.setVisible(true);
	}
}
