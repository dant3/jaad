package net.sourceforge.jaad;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.MetaData;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

public class MP4Info {

	private static final String USAGE = "usage:\nnet.sourceforge.jaad.MP4Info [-mp4] <infile> <outfile>\n\n\t-mp4\tinput file is in MP4 container format";

	public static void main(String[] args) {
		try {
			if(args.length<1) printUsage();
			else {
				final MP4Container cont = new MP4Container(new RandomAccessFile(args[0], "r"));
				final Movie movie = cont.getMovie();
				System.out.println("Movie:");

				final List<Track> tracks = movie.getTracks();
				Track t;
				for(int i = 0; i<tracks.size(); i++) {
					t = tracks.get(i);
					System.out.println("\tTrack "+i+": "+t.getCodec()+" (language="+t.getLanguage()+", created="+t.getCreationTime()+")");
				}

				final MetaData meta = movie.getMetaData();
				System.out.println("\tMetadata:");

				final Map<MetaData.Field<?>, Object> data = meta.getAll();
				for(MetaData.Field<?> key : data.keySet()) {
					System.out.println("\t\t"+key.getName()+" = "+data.get(key));
				}
			}
		}
		catch(Exception e) {
			System.err.println("error while decoding: "+e.toString());
		}
	}

	private static void printUsage() {
		System.out.println(USAGE);
		System.exit(1);
	}
}
