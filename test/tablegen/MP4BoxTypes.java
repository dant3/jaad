package tablegen;

public class MP4BoxTypes {

	private static final String[][] NAMES = {
		{"AUDIO_SAMPLE_ENTRY_BOX", "mp4a"},
		{"BIT_RATE_BOX", "btrt"},
		{"CHUNK_OFFSET_BOX", "stco"},
		{"CHUNK_LARGE_OFFSET_BOX", "co64"},
		{"CLEAN_APERTURE_BOX", "clap"},
		{"DATA_ENTRY_URN_BOX", "urn "},
		{"DATA_ENTRY_URL_BOX", "url "},
		{"DATA_INFORMATION_BOX", "dinf"},
		{"DATA_REFERENCE_BOX", "dref"},
		{"DEGRADATION_PRIORITY_BOX", "stdp"},
		{"ESD_BOX", "esds"},
		{"EXTENDED_BOX", "uuid"},
		{"FILE_TYPE_BOX", "ftyp"},
		{"HANDLER_BOX", "hdlr"},
		{"HINT_MEDIA_HEADER_BOX", "hmhd"},
		{"MEDIA_BOX", "mdia"},
		{"MEDIA_DATA_BOX", "mdat"},
		{"MEDIA_HEADER_BOX", "mdhd"},
		{"MEDIA_INFORMATION_BOX", "minf"},
		{"MOVIE_BOX", "moov"},
		{"MOVIE_EXTENDS_BOX", "mvex"},
		{"MOVIE_EXTENDS_HEADER_BOX", "mehd"},
		{"MOVIE_HEADER_BOX", "mvhd"},
		{"NULL_MEDIA_HEADER_BOX", "nmhd"},
		{"PADDING_BIT_BOX", "padp"},
		{"PROGRESSIVE_DOWNLOAD_INFORMATION_BOX", "pdin"},
		{"PIXEL_ASPECT_RATIO_BOX", "pasp"},
		{"SAMPLE_DESCRIPTION_BOX", "stsd"},
		{"SAMPLE_SIZE_BOX", "stsz"},
		{"SAMPLE_TABLE_BOX", "stbl"},
		{"SAMPLE_TO_CHUNK_BOX", "stsc"},
		{"SOUND_MEDIA_HEADER_BOX", "smhd"},
		{"TIME_TO_SAMPLE_BOX", "stts"},
		{"TRACK_BOX", "trak"},
		{"TRACK_HEADER_BOX", "tkhd"},
		{"VIDEO_MEDIA_HEADER_BOX", "vmhd"},
		{"TEXT_METADATA_SAMPLE_ENTRY", "mett"},
		{"XML_METADATA_SAMPLE_ENTRY", "metx"}
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
