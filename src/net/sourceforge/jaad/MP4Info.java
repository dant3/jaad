package net.sourceforge.jaad;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.MetaData;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;
import net.sourceforge.jaad.mp4.boxes.Box;
import net.sourceforge.jaad.mp4.boxes.BoxFactory;

public class MP4Info {

	private static final String USAGE = "usage:\nnet.sourceforge.jaad.MP4Info [options] <infile>\n\n\t-b\talso print all boxes";

	public static void main(String[] args) {
		try {
			if(args.length<1) printUsage();
			else {
				boolean boxes = false;
				final String file;
				if(args.length>1) {
					if(args[0].equals("-b")) boxes = true;
					else printUsage();
					file = args[1];
				}
				else file = args[0];

				final MP4Container cont = new MP4Container(new RandomAccessFile(file, "r"));
				final Movie movie = cont.getMovie();
				System.out.println("Movie:");

				final List<Track> tracks = movie.getTracks();
				Track t;
				for(int i = 0; i<tracks.size(); i++) {
					t = tracks.get(i);
					System.out.println("\tTrack "+i+": "+t.getCodec()+" (language: "+t.getLanguage()+", created: "+t.getCreationTime()+")");
				}

				final MetaData meta = movie.getMetaData();
				System.out.println("\tMetadata:");
				if(movie.containsMetaData()) {
					final Map<MetaData.Field<?>, Object> data = meta.getAll();
					for(MetaData.Field<?> key : data.keySet()) {
						System.out.println("\t\t"+key.getName()+" = "+data.get(key));
					}
				}
				else System.out.println("\t\tnone");

				if(boxes) {
					System.out.println("================================");
					for(Box box : cont.getBoxes()) {
						printBox(box, 0);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("error while reading file: "+e.toString());
		}
	}

	private static void printUsage() {
		System.out.println(USAGE);
		System.exit(1);
	}

	private static void printBox(Box box, int level) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i<level; i++) {
			sb.append("  ");
		}
		sb.append(box.getName());
		sb.append(" (");
		sb.append(BoxFactory.typeToString(box.getType()));
		sb.append(")");
		System.out.println(sb.toString());

		for(Box child : box.getChildren()) {
			printBox(child, level+1);
		}
	}
}
