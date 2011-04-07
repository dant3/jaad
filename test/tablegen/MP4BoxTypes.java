package tablegen;

import java.util.ArrayList;
import java.util.List;

public class MP4BoxTypes {

	private static final String[][] NAMES = {
		{"standard boxes"},
		{"ADDITIONAL_METADATA_CONTAINER_BOX", "meco", "BoxImpl.class", "Additional Metadata Container Box"},
		{"BINARY_XML_BOX", "bxml", "BinaryXMLBox.class"},
		{"BIT_RATE_BOX", "btrt", "BitRateBox.class"},
		{"CHUNK_OFFSET_BOX", "stco", "ChunkOffsetBox.class"},
		{"CHUNK_LARGE_OFFSET_BOX", "co64", "ChunkOffsetBox.class"},
		{"CLEAN_APERTURE_BOX", "clap", "CleanApertureBox.class"},
		{"COPYRIGHT_BOX", "cprt", "CopyrightBox.class"},
		{"DATA_ENTRY_URN_BOX", "urn ", "DataEntryUrnBox.class"},
		{"DATA_ENTRY_URL_BOX", "url ", "DataEntryUrlBox.class"},
		{"DATA_INFORMATION_BOX", "dinf", "BoxImpl.class", "Data Information Box"},
		{"DATA_REFERENCE_BOX", "dref", "DataReferenceBox.class"},
		{"DEGRADATION_PRIORITY_BOX", "stdp", "DegradationPriorityBox.class"},
		{"EDIT_BOX", "edts", "BoxImpl.class", "Edit Box"},
		{"EDIT_LIST_BOX", "elst", "EditListBox.class"},
		{"ESD_BOX", "esds", "ESDBox.class"},
		{"FILE_TYPE_BOX", "ftyp", "FileTypeBox.class"},
		{"FREE_SPACE_BOX", "free", "FreeSpaceBox.class"},
		{"HANDLER_BOX", "hdlr", "HandlerBox.class"},
		{"HINT_MEDIA_HEADER_BOX", "hmhd", "HintMediaHeaderBox.class"},
		{"ITEM_INFORMATION_BOX", "iinf", "ItemInformationBox.class"},
		{"ITEM_INFORMATION_ENTRY", "infe", "ItemInformationEntry.class"},
		{"ITEM_LOCATION_BOX", "iloc", "ItemLocationBox.class"},
		{"ITEM_PROTECTION_BOX", "ipro", "ItemProtectionBox.class"},
		{"MEDIA_BOX", "mdia", "BoxImpl.class", "Media Box"},
		{"MEDIA_DATA_BOX", "mdat", "MediaDataBox.class"},
		{"MEDIA_HEADER_BOX", "mdhd", "MediaHeaderBox.class"},
		{"MEDIA_INFORMATION_BOX", "minf", "BoxImpl.class", "Media Information Box"},
		{"META_BOX", "meta", "MetaBox.class"},
		{"META_BOX_RELATION_BOX", "mere", "MetaBoxRelationBox.class"},
		{"MOVIE_BOX", "moov", "BoxImpl.class", "Movie Box"},
		{"MOVIE_EXTENDS_BOX", "mvex", "BoxImpl.class", "Movie Extends Box"},
		{"MOVIE_EXTENDS_HEADER_BOX", "mehd", "MovieExtendsHeaderBox.class"},
		{"MOVIE_FRAGMENT_BOX", "moof", "BoxImpl.class", "Movie Fragment Box"},
		{"MOVIE_FRAGMENT_HEADER_BOX", "mfhd", "MovieFragmentHeaderBox.class"},
		{"MOVIE_HEADER_BOX", "mvhd", "MovieHeaderBox.class"},
		{"NULL_MEDIA_HEADER_BOX", "nmhd", "FullBox.class", "Null Media Header Box"},
		{"PADDING_BIT_BOX", "padb", "PaddingBitBox.class"},
		{"PIXEL_ASPECT_RATIO_BOX", "pasp", "PixelAspectRatioBox.class"},
		{"PRIMARY_ITEM_BOX", "pitm", "PrimaryItemBox.class"},
		{"PROGRESSIVE_DOWNLOAD_INFORMATION_BOX", "pdin", "ProgressiveDownloadInformationBox.class"},
		{"SAMPLE_DEPENDENCY_TYPE_BOX", "sdtp", "SampleDependencyTypeBox.class"},
		{"SAMPLE_DESCRIPTION_BOX", "stsd", "SampleDescriptionBox.class"},
		{"SAMPLE_GROUP_DESCRIPTION_BOX", "sgpd", "SampleGroupDescriptionBox.class"},
		{"SAMPLE_SCALE_BOX", "stsl", "SampleScaleBox.class"},
		{"SAMPLE_SIZE_BOX", "stsz", "SampleSizeBox.class"},
		{"SAMPLE_TABLE_BOX", "stbl", "BoxImpl.class", "Sample Table Box"},
		{"SAMPLE_TO_CHUNK_BOX", "stsc", "SampleToChunkBox.class"},
		{"SAMPLE_TO_GROUP_BOX", "sbgp", "SampleToGroupBox.class"},
		{"SHADOW_SYNC_SAMPLE_BOX", "stsh", "ShadowSyncSampleBox.class"},
		{"SKIP_BOX", "skip", "SkipBox.class"},
		{"SOUND_MEDIA_HEADER_BOX", "smhd", "SoundMediaHeaderBox.class"},
		{"SUB_SAMPLE_INFORMATION_BOX", "subs", "SubSampleInformationBox.class"},
		{"SYNC_SAMPLE_BOX", "stss", "SyncSampleBox.class"},
		{"TIME_TO_SAMPLE_BOX", "stts", "TimeToSampleBox.class"},
		{"TRACK_BOX", "trak", "BoxImpl.class", "Track Box"},
		{"TRACK_EXTENDS_BOX", "trex", "TrackExtendsBox.class"},
		{"TRACK_FRAGMENT_BOX", "traf", "BoxImpl.class", "Track Fragment Box"},
		{"TRACK_HEADER_BOX", "tkhd", "TrackHeaderBox.class"},
		{"TRACK_REFERENCE_BOX", "tref", "TrackReferenceBox.class"},
		{"TRACK_SELECTION_BOX", "tsel", "TrackSelectionBox.class"},
		{"USER_DATA_BOX", "udta", "BoxImpl.class", "User Data Box"},
		{"VIDEO_MEDIA_HEADER_BOX", "vmhd", "VideoMediaHeaderBox.class"},
		{"XML_BOX", "xml ", "XMLBox.class"},
		{"sample entries"},
		{"AUDIO_SAMPLE_ENTRY", "mp4a", "AudioSampleEntry.class"},
		{"TEXT_METADATA_SAMPLE_ENTRY", "mett", "TextMetadataSampleEntry.class"},
		{"XML_METADATA_SAMPLE_ENTRY", "metx", "XMLMetadataSampleEntry.class"},
		{"metadata extensions"},
		{"id3"},
		{"ID3_TAG_BOX", "id32", "ID3TagBox.class"},
		{"itunes"},
		{"ITUNES_META_LIST_BOX", "ilst", "BoxImpl.class", "iTunes Meta List Box"},
		{"TRACK_NAME_BOX", "©nam", "BoxImpl.class", "Track Name Box"},
		{"ARTIST_NAME_BOX", "©ART", "BoxImpl.class", "Artist Name BOx"},
		{"ALBUM_ARTIST_NAME_BOX", "aART", "BoxImpl.class", "Album Artist Name Box"},
		{"ALBUM_NAME_BOX", "©alb", "BoxImpl.class", "Album Name Box"},
		{"GROUPING_BOX", "©grp", "BoxImpl.class", "Grouping Box"},
		{"PUBLICATION_DATE_BOX", "©day", "BoxImpl.class", "Publication Date Box"},
		{"TRACK_NUMBER_BOX", "trkn", "BoxImpl.class", "Track Number Box"},
		{"DISK_NUMBER_BOX", "disk", "BoxImpl.class", "Disk Number Box"},
		{"TEMPO_BOX", "tmpo", "BoxImpl.class", "Tempo Box"},
		{"COMPOSER_NAME_BOX", "©wrt", "BoxImpl.class", "Composer Name Box"},
		{"COMMENTS_BOX", "©cmt", "BoxImpl.class", "Comments Box"},
		{"GENRE_BOX", "gnre", "BoxImpl.class", "Genre Box"},
		{"CUSTOM_GENRE_BOX", "©gen", "BoxImpl.class", "Custom Genre Box"},
		{"COMPILATION_PART_BOX", "cpil ", "BoxImpl.class", "Compilation Part Box"},
		{"TELEVISION_SHOW_BOX", "tvsh", "BoxImpl.class", "Television Show Box"},
		{"TRACK_SORT_BOX", "sonm", "BoxImpl.class", "Track Sort Box"},
		{"ARTIST_SORT_BOX", "soar", "BoxImpl.class", "Artist Sort Box"},
		{"ALBUM_ARTIST_SORT_BOX", "soaa ", "BoxImpl.class", "Album Artist Sort Box"},
		{"ALBUM_SORT_BOX", "soal", "BoxImpl.class", "Album Sort Box"},
		{"COMPOSER_SORT_BOX", "soco", "BoxImpl.class", "Composer Sort Box"},
		{"TELEVISION_SHOW_SORT_BOX", "sosn", "BoxImpl.class", "Television Show Sort Box"},
		{"LYRICS_BOX", "©lyr", "BoxImpl.class", "Lyrics Box"},
		{"COVER_BOX", "covr", "BoxImpl.class", "Cover Box"},
		{"SOFTWARE_INFORMATION_BOX", "©too", "BoxImpl.class", "Software Information Box"},
		{"CUSTOM_ITUNES_METADATA_BOX", "----", "BoxImpl.class", "Custom iTunes Metadata Box"},
		{"ITUNES_METADATA_BOX", "data", "ITunesMetadataBox.class"},
		{"ITUNES_METADATA_NAME_BOX", "name", "ITunesMetadataNameBox.class"}
	};

	public static void main(String[] args) {
		System.out.println("long EXTENDED_TYPE = "+toLong("uuid")+";");
		List<String> classes = new ArrayList<String>(0);
		List<String[]> params = new ArrayList<String[]>(0);

		for(int i = 0; i<NAMES.length; i++) {
			if(NAMES[i].length==1) System.out.println("\t//"+NAMES[i][0]);
			else {
				System.out.println("long "+NAMES[i][0]+" = "+toLong(NAMES[i][1])+"l; //"+NAMES[i][1]);
				classes.add(NAMES[i][0]+", "+NAMES[i][2]);
				if(NAMES[i].length>3) params.add(new String[]{NAMES[i][0], NAMES[i][3]});
			}
		}

		System.out.println("\n\nstatic {");
		System.out.println("//classes");
		for(int i = 0; i<classes.size(); i++) {
			System.out.println("BOX_CLASSES.put("+classes.get(i)+");");
		}
		System.out.println("//parameter");
		String[] s;
		for(int i = 0; i<params.size(); i++) {
			s = params.get(i);
			System.out.println("PARAMETER.put("+s[0]+", new String[]{\""+s[1]+"\"});");
		}
		System.out.println("}");
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
