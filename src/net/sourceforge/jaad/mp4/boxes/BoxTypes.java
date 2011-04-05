/*
 * Copyright (C) 2010 in-somnia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTYll; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad.mp4.boxes;

public interface BoxTypes {

	long EXTENDED_TYPE = 1970628964;
	//standard boxes
	long ADDITIONAL_METADATA_CONTAINER_BOX = 1835361135l; //meco
	long BINARY_XML_BOX = 1652059500l; //bxml
	long BIT_RATE_BOX = 1651798644l; //btrt
	long CHUNK_OFFSET_BOX = 1937007471l; //stco
	long CHUNK_LARGE_OFFSET_BOX = 1668232756l; //co64
	long CLEAN_APERTURE_BOX = 1668047216l; //clap
	long COPYRIGHT_BOX = 1668313716l; //cprt
	long DATA_ENTRY_URN_BOX = 1970433568l; //urn
	long DATA_ENTRY_URL_BOX = 1970433056l; //url
	long DATA_INFORMATION_BOX = 1684631142l; //dinf
	long DATA_REFERENCE_BOX = 1685218662l; //dref
	long DEGRADATION_PRIORITY_BOX = 1937007728l; //stdp
	long EDIT_BOX = 1701082227l; //edts
	long EDIT_LIST_BOX = 1701606260l; //elst
	long ESD_BOX = 1702061171l; //esds
	long FILE_TYPE_BOX = 1718909296l; //ftyp
	long FREE_SPACE_BOX = 1718773093l; //free
	long HANDLER_BOX = 1751411826l; //hdlr
	long HINT_MEDIA_HEADER_BOX = 1752000612l; //hmhd
	long ITEM_INFORMATION_BOX = 1768517222l; //iinf
	long ITEM_INFORMATION_ENTRY = 1768842853l; //infe
	long ITEM_LOCATION_BOX = 1768714083l; //iloc
	long ITEM_PROTECTION_BOX = 1768977007l; //ipro
	long MEDIA_BOX = 1835297121l; //mdia
	long MEDIA_DATA_BOX = 1835295092l; //mdat
	long MEDIA_HEADER_BOX = 1835296868l; //mdhd
	long MEDIA_INFORMATION_BOX = 1835626086l; //minf
	long META_BOX = 1835365473l; //meta
	long META_BOX_RELATION_BOX = 1835364965l; //mere
	long MOVIE_BOX = 1836019574l; //moov
	long MOVIE_EXTENDS_BOX = 1836475768l; //mvex
	long MOVIE_EXTENDS_HEADER_BOX = 1835362404l; //mehd
	long MOVIE_FRAGMENT_BOX = 1836019558l; //moof
	long MOVIE_FRAGMENT_HEADER_BOX = 1835427940l; //mfhd
	long MOVIE_HEADER_BOX = 1836476516l; //mvhd
	long NULL_MEDIA_HEADER_BOX = 1852663908l; //nmhd
	long PADDING_BIT_BOX = 1885430882l; //padb
	long PIXEL_ASPECT_RATIO_BOX = 1885434736l; //pasp
	long PRIMARY_ITEM_BOX = 1885959277l; //pitm
	long PROGRESSIVE_DOWNLOAD_INFORMATION_BOX = 1885628782l; //pdin
	long SAMPLE_DEPENDENCY_TYPE_BOX = 1935963248l; //sdtp
	long SAMPLE_DESCRIPTION_BOX = 1937011556l; //stsd
	long SAMPLE_GROUP_DESCRIPTION_BOX = 1936158820l; //sgpd
	long SAMPLE_SCALE_BOX = 1937011564l; //stsl
	long SAMPLE_SIZE_BOX = 1937011578l; //stsz
	long SAMPLE_TABLE_BOX = 1937007212l; //stbl
	long SAMPLE_TO_CHUNK_BOX = 1937011555l; //stsc
	long SAMPLE_TO_GROUP_BOX = 1935828848l; //sbgp
	long SHADOW_SYNC_SAMPLE_BOX = 1937011560l; //stsh
	long SKIP_BOX = 1936419184l; //skip
	long SOUND_MEDIA_HEADER_BOX = 1936549988l; //smhd
	long SUB_SAMPLE_INFORMATION_BOX = 1937072755l; //subs
	long SYNC_SAMPLE_BOX = 1937011571l; //stss
	long TIME_TO_SAMPLE_BOX = 1937011827l; //stts
	long TRACK_BOX = 1953653099l; //trak
	long TRACK_EXTENDS_BOX = 1953654136l; //trex
	long TRACK_FRAGMENT_BOX = 1953653094l; //traf
	long TRACK_HEADER_BOX = 1953196132l; //tkhd
	long TRACK_REFERENCE_BOX = 1953654118l; //tref
	long TRACK_SELECTION_BOX = 1953719660l; //tsel
	long USER_DATA_BOX = 1969517665l; //udta
	long VIDEO_MEDIA_HEADER_BOX = 1986881636l; //vmhd
	long XML_BOX = 2020437024l; //xml
	//sample entries
	long AUDIO_SAMPLE_ENTRY = 1836069985l; //mp4a
	long TEXT_METADATA_SAMPLE_ENTRY = 1835365492l; //mett
	long XML_METADATA_SAMPLE_ENTRY = 1835365496l; //metx
	//metadata extensions
	//id3
	long ID3_TAG_BOX = 1768174386l; //id32
	//itunes
	long ITUNES_META_LIST_BOX = 1768715124l; //ilst
	long TRACK_NAME_BOX = 2842583405l; //©nam
	long ARTIST_NAME_BOX = 2839630420l; //©ART
	long ALBUM_ARTIST_NAME_BOX = 1631670868l; //aART
	long ALBUM_NAME_BOX = 2841734242l; //©alb
	long GROUPING_BOX = 2842129008l; //©grp
	long PUBLICATION_DATE_BOX = 2841928057l; //©day
	long TRACK_NUMBER_BOX = 1953655662l; //trkn
	long DISK_NUMBER_BOX = 1684632427l; //disk
	long TEMPO_BOX = 1953329263l; //tmpo
	long COMPOSER_NAME_BOX = 2843177588l; //©wrt
	long COMMENTS_BOX = 2841865588l; //©cmt
	long GENRE_BOX = 1735291493l; //gnre
	long CUSTOM_GENRE_BOX = 2842125678l; //©gen
	long COMPILATION_PART_BOX = 1668311404l; //cpil
	long TELEVISION_SHOW_BOX = 1953919848l; //tvsh
	long TRACK_SORT_BOX = 1936682605l; //sonm
	long ARTIST_SORT_BOX = 1936679282l; //soar
	long ALBUM_ARTIST_SORT_BOX = 1936679265l; //soaa
	long ALBUM_SORT_BOX = 1936679276l; //soal
	long COMPOSER_SORT_BOX = 1936679791l; //soco
	long TELEVISION_SHOW_SORT_BOX = 1936683886l; //sosn
	long LYRICS_BOX = 2842458482l; //©lyr
	long COVER_BOX = 1668249202l; //covr
	long SOFTWARE_INFORMATION_BOX = 2842980207l; //©too
	long CUSTOM_ITUNES_METADATA_BOX = 757935405l; //----
	long ITUNES_METADATA_BOX = 1684108385l; //data
	long ITUNES_METADATA_NAME_BOX = 1851878757l; //name
}
