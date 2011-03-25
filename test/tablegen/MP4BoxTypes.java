package tablegen;

public class MP4BoxTypes {

	private static final String[][] NAMES = {
		{"AUDIO_SAMPLE_ENTRY_BOX", "mp4a"},
		{"CHUNK_OFFSET_BOX", "stco"},
		{"CHUNK_LARGE_OFFSET_BOX", "co64"},
		{"ESD_BOX", "esds"},
		{"EXTENDED_BOX", "uuid"},
		{"FILE_TYPE_BOX", "ftyp"},
		{"MEDIA_BOX", "mdia"},
		{"MEDIA_DATA_BOX", "mdat"},
		{"MEDIA_HEADER_BOX", "mdhd"},
		{"MEDIA_INFORMATION_BOX", "minf"},
		{"MOVIE_BOX", "moov"},
		{"MOVIE_HEADER_BOX", "mvhd"},
		{"PROGRESSIVE_DOWNLOAD_INFORMATION_BOX", "pdin"},
		{"SAMPLE_DESCRIPTION_BOX", "stsd"},
		{"SAMPLE_SIZE_BOX", "stsz"},
		{"SAMPLE_TABLE_BOX", "stbl"},
		{"SAMPLE_TO_CHUNK_BOX", "stsc"},
		{"SOUND_MEDIA_HEADER_BOX", "smhd"},
		{"TIME_TO_SAMPLE_BOX", "stts"},
		{"TRACK_BOX", "trak"},
		{"TRACK_HEADER_BOX", "tkhd"}
	};

	public static void main(String[] args) {
		for(int i = 0; i<NAMES.length; i++) {
			System.out.println("int "+NAMES[i][0]+" = "+toLong(NAMES[i][1])+"; //"+NAMES[i][1]);
		}
	}

	private static long toLong(String s) {
		long l = 0;
		for(int i = 0; i<4; i++) {
			l <<= 8;
			l |= s.charAt(i);
		}
		return l;
	}
}
