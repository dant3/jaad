package tablegen;

public class MP4BoxTypes {

	private static final String[][] NAMES = {
		{"EXTENDED_BOX", "uuid"},
		{"ADDITIONAL_METADATA_CONTAINER_BOX", "meco"},
		{"AUDIO_SAMPLE_ENTRY_BOX", "mp4a"},
		{"BINARY_XML_BOX", "bxml"},
		{"BIT_RATE_BOX", "btrt"},
		{"CHUNK_OFFSET_BOX", "stco"},
		{"CHUNK_LARGE_OFFSET_BOX", "co64"},
		{"CLEAN_APERTURE_BOX", "clap"},
		{"COPYRIGHT_BOX", "cprt"},
		{"DATA_ENTRY_URN_BOX", "urn "},
		{"DATA_ENTRY_URL_BOX", "url "},
		{"DATA_INFORMATION_BOX", "dinf"},
		{"DATA_REFERENCE_BOX", "dref"},
		{"DEGRADATION_PRIORITY_BOX", "stdp"},
		{"EDIT_BOX", "edts"},
		{"EDIT_LIST_BOX", "elst"},
		{"ESD_BOX", "esds"},
		{"FILE_TYPE_BOX", "ftyp"},
		{"FREE_SPACE_BOX", "free"},
		{"HANDLER_BOX", "hdlr"},
		{"HINT_MEDIA_HEADER_BOX", "hmhd"},
		{"ITEM_INFORMATION_BOX", "iinf"},
		{"ITEM_INFORMATION_ENTRY", "infe"},
		{"ITEM_LOCATION_BOX", "iloc"},
		{"ITEM_PROTECTION_BOX", "ipro"},
		{"MEDIA_BOX", "mdia"},
		{"MEDIA_DATA_BOX", "mdat"},
		{"MEDIA_HEADER_BOX", "mdhd"},
		{"MEDIA_INFORMATION_BOX", "minf"},
		{"META_BOX", "meta"},
		{"META_BOX_RELATION_BOX", "mere"},
		{"MOVIE_BOX", "moov"},
		{"MOVIE_EXTENDS_BOX", "mvex"},
		{"MOVIE_EXTENDS_HEADER_BOX", "mehd"},
		{"MOVIE_FRAGMENT_BOX", "moof"},
		{"MOVIE_FRAGMENT_HEADER_BOX", "mfhd"},
		{"MOVIE_HEADER_BOX", "mvhd"},
		{"NULL_MEDIA_HEADER_BOX", "nmhd"},
		{"PADDING_BIT_BOX", "padb"},
		{"PRIMARY_ITEM_BOX", "pitm"},
		{"PROGRESSIVE_DOWNLOAD_INFORMATION_BOX", "pdin"},
		{"PIXEL_ASPECT_RATIO_BOX", "pasp"},
		{"SAMPLE_DEPENDENCY_TYPE_BOX", "sdtp"},
		{"SAMPLE_DESCRIPTION_BOX", "stsd"},
		{"SAMPLE_GROUP_DESCRIPTION_BOX", "sgpd"},
		{"SAMPLE_SCALE_BOX", "stsl"},
		{"SAMPLE_SIZE_BOX", "stsz"},
		{"SAMPLE_TABLE_BOX", "stbl"},
		{"SAMPLE_TO_CHUNK_BOX", "stsc"},
		{"SAMPLE_TO_GROUP_BOX", "sbgp"},
		{"SHADOW_SYNC_SAMPLE_BOX", "stsh"},
		{"SKIP_BOX", "skip"},
		{"SOUND_MEDIA_HEADER_BOX", "smhd"},
		{"SUB_SAMPLE_INFORMATION_BOX", "subs"},
		{"SYNC_SAMPLE_BOX", "stss"},
		{"TIME_TO_SAMPLE_BOX", "stts"},
		{"TRACK_BOX", "trak"},
		{"TRACK_EXTENDS_BOX", "trex"},
		{"TRACK_FRAGMENT_BOX", "traf"},
		{"TRACK_HEADER_BOX", "tkhd"},
		{"TRACK_REFERENCE_BOX", "tref"},
		{"TRACK_SELECTION_BOX", "tsel"},
		{"USER_DATA_BOX", "udta"},
		{"VIDEO_MEDIA_HEADER_BOX", "vmhd"},
		{"TEXT_METADATA_SAMPLE_ENTRY", "mett"},
		{"XML_BOX", "xml "},
		{"XML_METADATA_SAMPLE_ENTRY", "metx"},};

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
