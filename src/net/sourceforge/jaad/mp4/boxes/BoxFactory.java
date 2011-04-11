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
package net.sourceforge.jaad.mp4.boxes;

import java.util.logging.Level;
import net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.sourceforge.jaad.mp4.boxes.impl.*;
import net.sourceforge.jaad.mp4.boxes.impl.meta.*;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.TextMetadataSampleEntry;
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.XMLMetadataSampleEntry;

public class BoxFactory implements BoxTypes {

	private static final Logger LOGGER = Logger.getLogger("net.sourceforge.jaad.util.mp4.boxes.BoxFactory");
	private static final Map<Long, Class<? extends BoxImpl>> BOX_CLASSES = new HashMap<Long, Class<? extends BoxImpl>>();
	private static final Map<Long, String[]> PARAMETER = new HashMap<Long, String[]>();

	static {
		//classes
		BOX_CLASSES.put(ADDITIONAL_METADATA_CONTAINER_BOX, BoxImpl.class);
		BOX_CLASSES.put(BINARY_XML_BOX, BinaryXMLBox.class);
		BOX_CLASSES.put(BIT_RATE_BOX, BitRateBox.class);
		BOX_CLASSES.put(CHUNK_OFFSET_BOX, ChunkOffsetBox.class);
		BOX_CLASSES.put(CHUNK_LARGE_OFFSET_BOX, ChunkOffsetBox.class);
		BOX_CLASSES.put(CLEAN_APERTURE_BOX, CleanApertureBox.class);
		BOX_CLASSES.put(COPYRIGHT_BOX, CopyrightBox.class);
		BOX_CLASSES.put(DATA_ENTRY_URN_BOX, DataEntryUrnBox.class);
		BOX_CLASSES.put(DATA_ENTRY_URL_BOX, DataEntryUrlBox.class);
		BOX_CLASSES.put(DATA_INFORMATION_BOX, BoxImpl.class);
		BOX_CLASSES.put(DATA_REFERENCE_BOX, DataReferenceBox.class);
		BOX_CLASSES.put(DEGRADATION_PRIORITY_BOX, DegradationPriorityBox.class);
		BOX_CLASSES.put(EDIT_BOX, BoxImpl.class);
		BOX_CLASSES.put(EDIT_LIST_BOX, EditListBox.class);
		BOX_CLASSES.put(ESD_BOX, ESDBox.class);
		BOX_CLASSES.put(FILE_TYPE_BOX, FileTypeBox.class);
		BOX_CLASSES.put(FREE_SPACE_BOX, FreeSpaceBox.class);
		BOX_CLASSES.put(HANDLER_BOX, HandlerBox.class);
		BOX_CLASSES.put(HINT_MEDIA_HEADER_BOX, HintMediaHeaderBox.class);
		BOX_CLASSES.put(ITEM_INFORMATION_BOX, ItemInformationBox.class);
		BOX_CLASSES.put(ITEM_INFORMATION_ENTRY, ItemInformationEntry.class);
		BOX_CLASSES.put(ITEM_LOCATION_BOX, ItemLocationBox.class);
		BOX_CLASSES.put(ITEM_PROTECTION_BOX, ItemProtectionBox.class);
		BOX_CLASSES.put(MEDIA_BOX, BoxImpl.class);
		BOX_CLASSES.put(MEDIA_DATA_BOX, MediaDataBox.class);
		BOX_CLASSES.put(MEDIA_HEADER_BOX, MediaHeaderBox.class);
		BOX_CLASSES.put(MEDIA_INFORMATION_BOX, BoxImpl.class);
		BOX_CLASSES.put(META_BOX, MetaBox.class);
		BOX_CLASSES.put(META_BOX_RELATION_BOX, MetaBoxRelationBox.class);
		BOX_CLASSES.put(MOVIE_BOX, BoxImpl.class);
		BOX_CLASSES.put(MOVIE_EXTENDS_BOX, BoxImpl.class);
		BOX_CLASSES.put(MOVIE_EXTENDS_HEADER_BOX, MovieExtendsHeaderBox.class);
		BOX_CLASSES.put(MOVIE_FRAGMENT_BOX, BoxImpl.class);
		BOX_CLASSES.put(MOVIE_FRAGMENT_HEADER_BOX, MovieFragmentHeaderBox.class);
		BOX_CLASSES.put(MOVIE_HEADER_BOX, MovieHeaderBox.class);
		BOX_CLASSES.put(NULL_MEDIA_HEADER_BOX, FullBox.class);
		BOX_CLASSES.put(PADDING_BIT_BOX, PaddingBitBox.class);
		BOX_CLASSES.put(PIXEL_ASPECT_RATIO_BOX, PixelAspectRatioBox.class);
		BOX_CLASSES.put(PRIMARY_ITEM_BOX, PrimaryItemBox.class);
		BOX_CLASSES.put(PROGRESSIVE_DOWNLOAD_INFORMATION_BOX, ProgressiveDownloadInformationBox.class);
		BOX_CLASSES.put(SAMPLE_DEPENDENCY_TYPE_BOX, SampleDependencyTypeBox.class);
		BOX_CLASSES.put(SAMPLE_DESCRIPTION_BOX, SampleDescriptionBox.class);
		BOX_CLASSES.put(SAMPLE_GROUP_DESCRIPTION_BOX, SampleGroupDescriptionBox.class);
		BOX_CLASSES.put(SAMPLE_SCALE_BOX, SampleScaleBox.class);
		BOX_CLASSES.put(SAMPLE_SIZE_BOX, SampleSizeBox.class);
		BOX_CLASSES.put(SAMPLE_TABLE_BOX, BoxImpl.class);
		BOX_CLASSES.put(SAMPLE_TO_CHUNK_BOX, SampleToChunkBox.class);
		BOX_CLASSES.put(SAMPLE_TO_GROUP_BOX, SampleToGroupBox.class);
		BOX_CLASSES.put(SHADOW_SYNC_SAMPLE_BOX, ShadowSyncSampleBox.class);
		BOX_CLASSES.put(SKIP_BOX, SkipBox.class);
		BOX_CLASSES.put(SOUND_MEDIA_HEADER_BOX, SoundMediaHeaderBox.class);
		BOX_CLASSES.put(SUB_SAMPLE_INFORMATION_BOX, SubSampleInformationBox.class);
		BOX_CLASSES.put(SYNC_SAMPLE_BOX, SyncSampleBox.class);
		BOX_CLASSES.put(TIME_TO_SAMPLE_BOX, TimeToSampleBox.class);
		BOX_CLASSES.put(TRACK_BOX, BoxImpl.class);
		BOX_CLASSES.put(TRACK_EXTENDS_BOX, TrackExtendsBox.class);
		BOX_CLASSES.put(TRACK_FRAGMENT_BOX, BoxImpl.class);
		BOX_CLASSES.put(TRACK_HEADER_BOX, TrackHeaderBox.class);
		BOX_CLASSES.put(TRACK_REFERENCE_BOX, TrackReferenceBox.class);
		BOX_CLASSES.put(TRACK_SELECTION_BOX, TrackSelectionBox.class);
		BOX_CLASSES.put(USER_DATA_BOX, BoxImpl.class);
		BOX_CLASSES.put(VIDEO_MEDIA_HEADER_BOX, VideoMediaHeaderBox.class);
		BOX_CLASSES.put(XML_BOX, XMLBox.class);
		BOX_CLASSES.put(AUDIO_SAMPLE_ENTRY, AudioSampleEntry.class);
		BOX_CLASSES.put(TEXT_METADATA_SAMPLE_ENTRY, TextMetadataSampleEntry.class);
		BOX_CLASSES.put(XML_METADATA_SAMPLE_ENTRY, XMLMetadataSampleEntry.class);
		BOX_CLASSES.put(ID3_TAG_BOX, ID3TagBox.class);
		BOX_CLASSES.put(ITUNES_META_LIST_BOX, BoxImpl.class);
		BOX_CLASSES.put(TRACK_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(ARTIST_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(ALBUM_ARTIST_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(ALBUM_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(GROUPING_BOX, BoxImpl.class);
		BOX_CLASSES.put(PUBLICATION_YEAR_BOX, BoxImpl.class);
		BOX_CLASSES.put(TRACK_NUMBER_BOX, BoxImpl.class);
		BOX_CLASSES.put(DISK_NUMBER_BOX, BoxImpl.class);
		BOX_CLASSES.put(TEMPO_BOX, BoxImpl.class);
		BOX_CLASSES.put(COMPOSER_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(COMMENTS_BOX, BoxImpl.class);
		BOX_CLASSES.put(GENRE_BOX, BoxImpl.class);
		BOX_CLASSES.put(CUSTOM_GENRE_BOX, BoxImpl.class);
		BOX_CLASSES.put(COMPILATION_PART_BOX, BoxImpl.class);
		BOX_CLASSES.put(TELEVISION_SHOW_BOX, BoxImpl.class);
		BOX_CLASSES.put(TRACK_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(ARTIST_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(ALBUM_ARTIST_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(ALBUM_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(COMPOSER_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(TELEVISION_SHOW_SORT_BOX, BoxImpl.class);
		BOX_CLASSES.put(LYRICS_BOX, BoxImpl.class);
		BOX_CLASSES.put(COVER_BOX, BoxImpl.class);
		BOX_CLASSES.put(ENCODER_TOOL_BOX, BoxImpl.class);
		BOX_CLASSES.put(RATING_BOX, BoxImpl.class);
		BOX_CLASSES.put(PODCAST_BOX, BoxImpl.class);
		BOX_CLASSES.put(PODCAST_URL_BOX, BoxImpl.class);
		BOX_CLASSES.put(CATEGORY_BOX, BoxImpl.class);
		BOX_CLASSES.put(KEYWORD_BOX, BoxImpl.class);
		BOX_CLASSES.put(EPISODE_GLOBAL_UNIQUE_ID_BOX, BoxImpl.class);
		BOX_CLASSES.put(DESCRIPTION_BOX, BoxImpl.class);
		BOX_CLASSES.put(TV_NETWORK_NAME_BOX, BoxImpl.class);
		BOX_CLASSES.put(TV_EPISODE_NUMBER_BOX, BoxImpl.class);
		BOX_CLASSES.put(TV_SEASON_BOX, BoxImpl.class);
		BOX_CLASSES.put(TV_EPISODE_BOX, BoxImpl.class);
		BOX_CLASSES.put(PURCHASE_DATE_BOX, BoxImpl.class);
		BOX_CLASSES.put(GAPLESS_PLAYBACK_BOX, BoxImpl.class);
		BOX_CLASSES.put(CUSTOM_ITUNES_METADATA_BOX, BoxImpl.class);
		BOX_CLASSES.put(ITUNES_METADATA_BOX, ITunesMetadataBox.class);
		BOX_CLASSES.put(ITUNES_METADATA_NAME_BOX, ITunesMetadataNameBox.class);
		//parameter
		PARAMETER.put(ADDITIONAL_METADATA_CONTAINER_BOX, new String[]{"Additional Metadata Container Box"});
		PARAMETER.put(DATA_INFORMATION_BOX, new String[]{"Data Information Box"});
		PARAMETER.put(EDIT_BOX, new String[]{"Edit Box"});
		PARAMETER.put(MEDIA_BOX, new String[]{"Media Box"});
		PARAMETER.put(MEDIA_INFORMATION_BOX, new String[]{"Media Information Box"});
		PARAMETER.put(MOVIE_BOX, new String[]{"Movie Box"});
		PARAMETER.put(MOVIE_EXTENDS_BOX, new String[]{"Movie Extends Box"});
		PARAMETER.put(MOVIE_FRAGMENT_BOX, new String[]{"Movie Fragment Box"});
		PARAMETER.put(NULL_MEDIA_HEADER_BOX, new String[]{"Null Media Header Box"});
		PARAMETER.put(SAMPLE_TABLE_BOX, new String[]{"Sample Table Box"});
		PARAMETER.put(TRACK_BOX, new String[]{"Track Box"});
		PARAMETER.put(TRACK_FRAGMENT_BOX, new String[]{"Track Fragment Box"});
		PARAMETER.put(USER_DATA_BOX, new String[]{"User Data Box"});
		PARAMETER.put(ITUNES_META_LIST_BOX, new String[]{"iTunes Meta List Box"});
		PARAMETER.put(TRACK_NAME_BOX, new String[]{"Track Name Box"});
		PARAMETER.put(ARTIST_NAME_BOX, new String[]{"Artist Name BOx"});
		PARAMETER.put(ALBUM_ARTIST_NAME_BOX, new String[]{"Album Artist Name Box"});
		PARAMETER.put(ALBUM_NAME_BOX, new String[]{"Album Name Box"});
		PARAMETER.put(GROUPING_BOX, new String[]{"Grouping Box"});
		PARAMETER.put(PUBLICATION_YEAR_BOX, new String[]{"Publication Year Box"});
		PARAMETER.put(TRACK_NUMBER_BOX, new String[]{"Track Number Box"});
		PARAMETER.put(DISK_NUMBER_BOX, new String[]{"Disk Number Box"});
		PARAMETER.put(TEMPO_BOX, new String[]{"Tempo Box"});
		PARAMETER.put(COMPOSER_NAME_BOX, new String[]{"Composer Name Box"});
		PARAMETER.put(COMMENTS_BOX, new String[]{"Comments Box"});
		PARAMETER.put(GENRE_BOX, new String[]{"Genre Box"});
		PARAMETER.put(CUSTOM_GENRE_BOX, new String[]{"Custom Genre Box"});
		PARAMETER.put(COMPILATION_PART_BOX, new String[]{"Compilation Part Box"});
		PARAMETER.put(TELEVISION_SHOW_BOX, new String[]{"Television Show Box"});
		PARAMETER.put(TRACK_SORT_BOX, new String[]{"Track Sort Box"});
		PARAMETER.put(ARTIST_SORT_BOX, new String[]{"Artist Sort Box"});
		PARAMETER.put(ALBUM_ARTIST_SORT_BOX, new String[]{"Album Artist Sort Box"});
		PARAMETER.put(ALBUM_SORT_BOX, new String[]{"Album Sort Box"});
		PARAMETER.put(COMPOSER_SORT_BOX, new String[]{"Composer Sort Box"});
		PARAMETER.put(TELEVISION_SHOW_SORT_BOX, new String[]{"Television Show Sort Box"});
		PARAMETER.put(LYRICS_BOX, new String[]{"Lyrics Box"});
		PARAMETER.put(COVER_BOX, new String[]{"Cover Box"});
		PARAMETER.put(ENCODER_TOOL_BOX, new String[]{"Encoder Tool Box"});
		PARAMETER.put(RATING_BOX, new String[]{"Rating Box"});
		PARAMETER.put(PODCAST_BOX, new String[]{"Podcast Box"});
		PARAMETER.put(PODCAST_URL_BOX, new String[]{"Podcast URL Box"});
		PARAMETER.put(CATEGORY_BOX, new String[]{"Category Box"});
		PARAMETER.put(KEYWORD_BOX, new String[]{"Keyword Box"});
		PARAMETER.put(EPISODE_GLOBAL_UNIQUE_ID_BOX, new String[]{"Episode Global Unique ID Box"});
		PARAMETER.put(DESCRIPTION_BOX, new String[]{"Description Cover Box"});
		PARAMETER.put(TV_NETWORK_NAME_BOX, new String[]{"TV Network Name Box"});
		PARAMETER.put(TV_EPISODE_NUMBER_BOX, new String[]{"TV Episode Number Box"});
		PARAMETER.put(TV_SEASON_BOX, new String[]{"TV Season Box"});
		PARAMETER.put(TV_EPISODE_BOX, new String[]{"TV Episode Box"});
		PARAMETER.put(PURCHASE_DATE_BOX, new String[]{"Purchase Date Box"});
		PARAMETER.put(GAPLESS_PLAYBACK_BOX, new String[]{"Gapless Playback Box"});
		PARAMETER.put(CUSTOM_ITUNES_METADATA_BOX, new String[]{"Custom iTunes Metadata Box"});
	}

	public static Box parseBox(Box parent, MP4InputStream in) throws IOException {
		long size = in.readBytes(4);
		long left = size-4;
		if(size==1) {
			size = in.readBytes(8);
			left -= 8;
		}
		long type = in.readBytes(4);
		left -= 4;
		if(type==EXTENDED_TYPE) {
			type = in.readBytes(16);
			left -= 16;
		}

		final BoxImpl box = forType(type);
		box.setParams(size, type, parent, left);
		box.decode(in);
		//DEBUG:
		//System.out.println(box.getShortName());

		//if mdat found, don't skip
		//TODO: what if random access can be used??
		left = box.getLeft();
		if(left<0) LOGGER.log(Level.WARNING, "box: {0}, left: {1}, offset: {2}", new String[]{typeToString(type), Long.toString(left), Long.toString(in.getOffset())});
		if(box.getType()!=MEDIA_DATA_BOX) in.skipBytes(left);
		return box;
	}

	public static Box parseBox(MP4InputStream in, Class<? extends BoxImpl> boxClass) throws IOException {
		long size = in.readBytes(4);
		long left = size-4;
		if(size==1) {
			size = in.readBytes(8);
			left -= 8;
		}
		long type = in.readBytes(4);
		left -= 4;
		if(type==EXTENDED_TYPE) {
			type = in.readBytes(16);
			left -= 16;
		}

		BoxImpl box = null;
		try {
			box = boxClass.newInstance();
		}
		catch(InstantiationException ex) {
		}
		catch(IllegalAccessException ex) {
		}

		if(box!=null) {
			box.setParams(size, type, null, left);
			box.decode(in);
			in.skipBytes(box.getLeft());
			//DEBUG:
			//System.out.println(box.getShortName());
		}
		return box;
	}

	private static BoxImpl forType(long type) {
		BoxImpl box = null;

		final Long l = Long.valueOf(type);
		if(BOX_CLASSES.containsKey(l)) {
			Class<? extends BoxImpl> cl = BOX_CLASSES.get(l);
			if(PARAMETER.containsKey(l)) {
				final String[] s = PARAMETER.get(l);
				try {
					Constructor<? extends BoxImpl> con = cl.getConstructor(String.class);
					box = con.newInstance(s[0]);
				}
				catch(Exception e) {
					LOGGER.log(Level.WARNING, "could not call constructor for "+typeToString(type), e);
					box = new UnknownBox();
				}
			}
			else {
				try {
					box = cl.newInstance();
				}
				catch(Exception e) {
					LOGGER.log(Level.WARNING, "could not instantiate box "+typeToString(type), e);
				}
			}
		}

		if(box==null) box = new UnknownBox();
		return box;
	}

	public static String typeToString(long l) {
		byte[] b = new byte[4];
		b[0] = (byte) ((l>>24)&0xFF);
		b[1] = (byte) ((l>>16)&0xFF);
		b[2] = (byte) ((l>>8)&0xFF);
		b[3] = (byte) (l&0xFF);
		return new String(b);
	}
}
