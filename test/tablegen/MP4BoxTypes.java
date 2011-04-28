package tablegen;

import java.util.ArrayList;
import java.util.List;

public class MP4BoxTypes {

	private static final String[][] NAMES = {
		{"standard boxes (ISO BMFF)"},
		{"ADDITIONAL_METADATA_CONTAINER_BOX", "meco", "BoxImpl", "Additional Metadata Container Box"},
		{"BINARY_XML_BOX", "bxml", "BinaryXMLBox"},
		{"BIT_RATE_BOX", "btrt", "BitRateBox"},
		{"CHAPTER_BOX", "chpl", "ChapterBox"},
		{"CHUNK_OFFSET_BOX", "stco", "ChunkOffsetBox"},
		{"CHUNK_LARGE_OFFSET_BOX", "co64", "ChunkOffsetBox"},
		{"CLEAN_APERTURE_BOX", "clap", "CleanApertureBox"},
		{"COMPACT_SAMPLE_SIZE_BOX", "stz2", "SampleSizeBox"},
		{"COMPOSITION_TIME_TO_SAMPLE_BOX", "ctts", "CompositionTimeToSampleBox"},
		{"COPYRIGHT_BOX", "cprt", "CopyrightBox"},
		{"DATA_ENTRY_URN_BOX", "urn ", "DataEntryUrnBox"},
		{"DATA_ENTRY_URL_BOX", "url ", "DataEntryUrlBox"},
		{"DATA_INFORMATION_BOX", "dinf", "BoxImpl", "Data Information Box"},
		{"DATA_REFERENCE_BOX", "dref", "DataReferenceBox"},
		{"DECODING_TIME_TO_SAMPLE_BOX", "stts", "DecodingTimeToSampleBox"},
		{"DEGRADATION_PRIORITY_BOX", "stdp", "DegradationPriorityBox"},
		{"EDIT_BOX", "edts", "BoxImpl", "Edit Box"},
		{"EDIT_LIST_BOX", "elst", "EditListBox"},
		{"FD_ITEM_INFORMATION_BOX", "fiin", "FDItemInformationBox"},
		{"FD_SESSION_GROUP_BOX", "segr", "FDSessionGroupBox"},
		{"FEC_RESERVOIR_BOX", "fecr", "FECReservoirBox"},
		{"FILE_PARTITION_BOX", "fpar", "FilePartitionBox"},
		{"FILE_TYPE_BOX", "ftyp", "FileTypeBox"},
		{"FREE_SPACE_BOX", "free", "FreeSpaceBox"},
		{"GROUP_ID_TO_NAME_BOX", "gitn", "GroupIDToNameBox"},
		{"HANDLER_BOX", "hdlr", "HandlerBox"},
		{"HINT_MEDIA_HEADER_BOX", "hmhd", "HintMediaHeaderBox"},
		{"IPMP_CONTROL_BOX", "ipmc", "IPMPControlBox"},
		{"IPMP_INFO_BOX", "imif", "IPMPInfoBox"},
		{"ITEM_INFORMATION_BOX", "iinf", "ItemInformationBox"},
		{"ITEM_INFORMATION_ENTRY", "infe", "ItemInformationEntry"},
		{"ITEM_LOCATION_BOX", "iloc", "ItemLocationBox"},
		{"ITEM_PROTECTION_BOX", "ipro", "ItemProtectionBox"},
		{"MEDIA_BOX", "mdia", "BoxImpl", "Media Box"},
		{"MEDIA_DATA_BOX", "mdat", "MediaDataBox"},
		{"MEDIA_HEADER_BOX", "mdhd", "MediaHeaderBox"},
		{"MEDIA_INFORMATION_BOX", "minf", "BoxImpl", "Media Information Box"},
		{"META_BOX", "meta", "MetaBox"},
		{"META_BOX_RELATION_BOX", "mere", "MetaBoxRelationBox"},
		{"MOVIE_BOX", "moov", "BoxImpl", "Movie Box"},
		{"MOVIE_EXTENDS_BOX", "mvex", "BoxImpl", "Movie Extends Box"},
		{"MOVIE_EXTENDS_HEADER_BOX", "mehd", "MovieExtendsHeaderBox"},
		{"MOVIE_FRAGMENT_BOX", "moof", "BoxImpl", "Movie Fragment Box"},
		{"MOVIE_FRAGMENT_HEADER_BOX", "mfhd", "MovieFragmentHeaderBox"},
		{"MOVIE_FRAGMENT_RANDOM_ACCESS_BOX", "mfra", "BoxImpl", "Movie Fragment Random Access Box"},
		{"MOVIE_FRAGMENT_RANDOM_ACCESS_OFFSET_BOX", "mfro", "MovieFragmentRandomAccessOffsetBox"},
		{"MOVIE_HEADER_BOX", "mvhd", "MovieHeaderBox"},
		{"NERO_METADATA_TAGS_BOX", "tags", "NeroMetadataTagsBox"},
		{"NULL_MEDIA_HEADER_BOX", "nmhd", "FullBox", "Null Media Header Box"},
		{"ORIGINAL_FORMAT_BOX", "frma", "OriginalFormatBox"},
		{"PADDING_BIT_BOX", "padb", "PaddingBitBox"},
		{"PARTITION_ENTRY", "paen", "BoxImpl", "Partition Entry"},
		{"PIXEL_ASPECT_RATIO_BOX", "pasp", "PixelAspectRatioBox"},
		{"PRIMARY_ITEM_BOX", "pitm", "PrimaryItemBox"},
		{"PROGRESSIVE_DOWNLOAD_INFORMATION_BOX", "pdin", "ProgressiveDownloadInformationBox"},
		{"PROTECTION_SCHEME_INFORMATION_BOX", "sinf", "BoxImpl", "Protection Scheme Information Box"},
		{"SAMPLE_DEPENDENCY_TYPE_BOX", "sdtp", "SampleDependencyTypeBox"},
		{"SAMPLE_DESCRIPTION_BOX", "stsd", "SampleDescriptionBox"},
		{"SAMPLE_GROUP_DESCRIPTION_BOX", "sgpd", "SampleGroupDescriptionBox"},
		{"SAMPLE_SCALE_BOX", "stsl", "SampleScaleBox"},
		{"SAMPLE_SIZE_BOX", "stsz", "SampleSizeBox"},
		{"SAMPLE_TABLE_BOX", "stbl", "BoxImpl", "Sample Table Box"},
		{"SAMPLE_TO_CHUNK_BOX", "stsc", "SampleToChunkBox"},
		{"SAMPLE_TO_GROUP_BOX", "sbgp", "SampleToGroupBox"},
		{"SCHEME_TYPE_BOX", "schm", "SchemeTypeBox"},
		{"SCHEME_INFORMATION_BOX", "schi", "BoxImpl", "Scheme Information Box"},
		{"SHADOW_SYNC_SAMPLE_BOX", "stsh", "ShadowSyncSampleBox"},
		{"SKIP_BOX", "skip", "SkipBox"},
		{"SOUND_MEDIA_HEADER_BOX", "smhd", "SoundMediaHeaderBox"},
		{"SUB_SAMPLE_INFORMATION_BOX", "subs", "SubSampleInformationBox"},
		{"SYNC_SAMPLE_BOX", "stss", "SyncSampleBox"},
		{"TRACK_BOX", "trak", "BoxImpl", "Track Box"},
		{"TRACK_EXTENDS_BOX", "trex", "TrackExtendsBox"},
		{"TRACK_FRAGMENT_BOX", "traf", "BoxImpl", "Track Fragment Box"},
		{"TRACK_FRAGMENT_HEADER_BOX", "tfhd", "TrackFragmentHeaderBox"},
		{"TRACK_FRAGMENT_RANDOM_ACCESS_BOX", "tfra", "TrackFragmentRandomAccessBox"},
		{"TRACK_FRAGMENT_RUN_BOX", "trun", "TrackFragmentRunBox"},
		{"TRACK_HEADER_BOX", "tkhd", "TrackHeaderBox"},
		{"TRACK_REFERENCE_BOX", "tref", "TrackReferenceBox"},
		{"TRACK_SELECTION_BOX", "tsel", "TrackSelectionBox"},
		{"USER_DATA_BOX", "udta", "BoxImpl", "User Data Box"},
		{"VIDEO_MEDIA_HEADER_BOX", "vmhd", "VideoMediaHeaderBox"},
		{"XML_BOX", "xml ", "XMLBox"},
		{"mp4 extension"},
		{"OBJECT_DESCRIPTOR_BOX", "iods", "ObjectDescriptorBox"},
		{"SAMPLE_DEPENDENCY_BOX", "sdep", "SampleDependencyBox"},
		{"metadata: id3"},
		{"ID3_TAG_BOX", "id32", "ID3TagBox"},
		{"metadata: itunes"},
		{"ITUNES_META_LIST_BOX", "ilst", "BoxImpl", "iTunes Meta List Box"},
		{"CUSTOM_ITUNES_METADATA_BOX", "----", "BoxImpl", "Custom iTunes Metadata Box"},
		{"ITUNES_METADATA_BOX", "data", "ITunesMetadataBox"},
		{"ITUNES_METADATA_NAME_BOX", "name", "ITunesMetadataNameBox"},
		{"ALBUM_ARTIST_NAME_BOX", "aART", "BoxImpl", "Album Artist Name Box"},
		{"ALBUM_ARTIST_SORT_BOX", "soaa ", "BoxImpl", "Album Artist Sort Box"},
		{"ALBUM_NAME_BOX", "©alb", "BoxImpl", "Album Name Box"},
		{"ALBUM_SORT_BOX", "soal", "BoxImpl", "Album Sort Box"},
		{"ARTIST_NAME_BOX", "©ART", "BoxImpl", "Artist Name Box"},
		{"ARTIST_SORT_BOX", "soar", "BoxImpl", "Artist Sort Box"},
		{"CATEGORY_BOX", "catg", "BoxImpl", "Category Box"},
		{"COMMENTS_BOX", "©cmt", "BoxImpl", "Comments Box"},
		{"COMPILATION_PART_BOX", "cpil ", "BoxImpl", "Compilation Part Box"},
		{"COMPOSER_NAME_BOX", "©wrt", "BoxImpl", "Composer Name Box"},
		{"COMPOSER_SORT_BOX", "soco", "BoxImpl", "Composer Sort Box"},
		{"COVER_BOX", "covr", "BoxImpl", "Cover Box"},
		{"CUSTOM_GENRE_BOX", "©gen", "BoxImpl", "Custom Genre Box"},
		{"DESCRIPTION_BOX", "desc", "BoxImpl", "Description Cover Box"},
		{"DISK_NUMBER_BOX", "disk", "BoxImpl", "Disk Number Box"},
		{"ENCODER_NAME_BOX", "©enc", "BoxImpl", "Encoder Name Box"},
		{"ENCODER_TOOL_BOX", "©too", "BoxImpl", "Encoder Tool Box"},
		{"EPISODE_GLOBAL_UNIQUE_ID_BOX", "egid", "BoxImpl", "Episode Global Unique ID Box"},
		{"GAPLESS_PLAYBACK_BOX", "pgap", "BoxImpl", "Gapless Playback Box"},
		{"GENRE_BOX", "gnre", "BoxImpl", "Genre Box"},
		{"GROUPING_BOX", "©grp", "BoxImpl", "Grouping Box"},
		{"HD_VIDEO_BOX", "hdvd", "BoxImpl", "HD Video Box"},
		{"ITUNES_PURCHASE_ACCOUNT_BOX", "apID", "BoxImpl", "iTunes Purchase Account Box"},
		{"ITUNES_ACCOUNT_TYPE_BOX", "akID", "BoxImpl", "iTunes Account Type Box"},
		{"ITUNES_CATALOGUE_ID_BOX", "cnID", "BoxImpl", "iTunes Catalogue ID Box"},
		{"ITUNES_COUNTRY_CODE_BOX", "sfID", "BoxImpl", "iTunes Country Code Box"},
		{"KEYWORD_BOX", "keyw", "BoxImpl", "Keyword Box"},
		{"LONG_DESCRIPTION_BOX", "ldes", "BoxImpl", "Long Description Box"},
		{"LYRICS_BOX", "©lyr", "BoxImpl", "Lyrics Box"},
		{"META_TYPE_BOX", "stik", "BoxImpl", "Meta Type Box"},
		{"PODCAST_BOX", "pcst", "BoxImpl", "Podcast Box"},
		{"PODCAST_URL_BOX", "purl", "BoxImpl", "Podcast URL Box"},
		{"PURCHASE_DATE_BOX", "purd", "BoxImpl", "Purchase Date Box"},
		{"RATING_BOX", "rtng", "BoxImpl", "Rating Box"},
		{"RELEASE_DATE_BOX", "©day", "BoxImpl", "Release Date Box"},
		{"TEMPO_BOX", "tmpo", "BoxImpl", "Tempo Box"},
		{"TRACK_NAME_BOX", "©nam", "BoxImpl", "Track Name Box"},
		{"TRACK_NUMBER_BOX", "trkn", "BoxImpl", "Track Number Box"},
		{"TRACK_SORT_BOX", "sonm", "BoxImpl", "Track Sort Box"},
		{"TV_EPISODE_BOX", "tves", "BoxImpl", "TV Episode Box"},
		{"TV_EPISODE_NUMBER_BOX", "tven", "BoxImpl", "TV Episode Number Box"},
		{"TV_NETWORK_NAME_BOX", "tvnn", "BoxImpl", "TV Network Name Box"},
		{"TV_SEASON_BOX", "tvsn", "BoxImpl", "TV Season Box"},
		{"TV_SHOW_BOX", "tvsh", "BoxImpl", "TV Show Box"},
		{"TV_SHOW_SORT_BOX", "sosn", "BoxImpl", "TV Show Sort Box"},
		{"sample entries"},
		{"MP4V_SAMPLE_ENTRY", "mp4v", "VideoSampleEntry"},
		{"H263_SAMPLE_ENTRY", "s263", "VideoSampleEntry"},
		{"AVC_SAMPLE_ENTRY", "avc1", "VideoSampleEntry"},
		{"MP4A_SAMPLE_ENTRY", "mp4a", "AudioSampleEntry"},
		{"AMR_SAMPLE_ENTRY", "samr", "AudioSampleEntry"},
		{"AMR_WB_SAMPLE_ENTRY", "sawb", "AudioSampleEntry"},
		{"EVRC_SAMPLE_ENTRY", "sevc", "AudioSampleEntry"},
		{"QCELP_SAMPLE_ENTRY", "sqcp", "AudioSampleEntry"},
		{"SMV_SAMPLE_ENTRY", "ssmv", "AudioSampleEntry"},
		{"MPEG_SAMPLE_ENTRY", "mp4s", "MPEGSampleEntry"},
		{"TEXT_METADATA_SAMPLE_ENTRY", "mett", "TextMetadataSampleEntry"},
		{"XML_METADATA_SAMPLE_ENTRY", "metx", "XMLMetadataSampleEntry"},
		{"RTP_HINT_SAMPLE_ENTRY", "rtp ", "RTPHintSampleEntry"},
		{"FD_HINT_SAMPLE_ENTRY", "fdp ", "FDHintSampleEntry"},
		{"codec infos"},
		{"ESD_BOX", "esds", "ESDBox"},
		{"video codecs"},
		{"H263_SPECIFIC_BOX", "d263", "CodecSpecificBox"},
		{"AVC_SPECIFIC_BOX", "avcC", "CodecSpecificBox"},
		{"audio codecs"},
		{"AMR_SPECIFIC_BOX", "damr", "CodecSpecificBox"},
		{"EVRC_SPECIFIC_BOX", "devc", "CodecSpecificBox"},
		{"QCELP_SPECIFIC_BOX", "dqcp", "CodecSpecificBox"},
		{"SMV_SPECIFIC_BOX", "dsmv", "CodecSpecificBox"}
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
		System.out.println("\t\t//classes");
		for(int i = 0; i<classes.size(); i++) {
			System.out.println("BOX_CLASSES.put("+classes.get(i)+".class);");
		}
		System.out.println("\t\t//parameter");
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
