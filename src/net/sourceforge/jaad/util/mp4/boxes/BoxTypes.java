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
package net.sourceforge.jaad.util.mp4.boxes;

public interface BoxTypes {

	int AUDIO_SAMPLE_ENTRY_BOX = 1836069985; //mp4a
	int BIT_RATE_BOX = 1651798644; //btrt
	int CHUNK_OFFSET_BOX = 1937007471; //stco
	int CHUNK_LARGE_OFFSET_BOX = 1668232756; //co64
	int CLEAN_APERTURE_BOX = 1668047216; //clap
	int DATA_ENTRY_URN_BOX = 1970433568; //urn
	int DATA_ENTRY_URL_BOX = 1970433056; //url
	int DATA_INFORMATION_BOX = 1684631142; //dinf
	int DATA_REFERENCE_BOX = 1685218662; //dref
	int DEGRADATION_PRIORITY_BOX = 1937007728; //stdp
	int ESD_BOX = 1702061171; //esds
	int EXTENDED_BOX = 1970628964; //uuid
	int FILE_TYPE_BOX = 1718909296; //ftyp
	int HANDLER_BOX = 1751411826; //hdlr
	int HINT_MEDIA_HEADER_BOX = 1752000612; //hmhd
	int MEDIA_BOX = 1835297121; //mdia
	int MEDIA_DATA_BOX = 1835295092; //mdat
	int MEDIA_HEADER_BOX = 1835296868; //mdhd
	int MEDIA_INFORMATION_BOX = 1835626086; //minf
	int MOVIE_BOX = 1836019574; //moov
	int MOVIE_EXTENDS_BOX = 1836475768; //mvex
	int MOVIE_EXTENDS_HEADER_BOX = 1835362404; //mehd
	int MOVIE_HEADER_BOX = 1836476516; //mvhd
	int NULL_MEDIA_HEADER_BOX = 1852663908; //nmhd
	int PADDING_BIT_BOX = 1885430896; //padp
	int PROGRESSIVE_DOWNLOAD_INFORMATION_BOX = 1885628782; //pdin
	int PIXEL_ASPECT_RATIO_BOX = 1885434736; //pasp
	int SAMPLE_DESCRIPTION_BOX = 1937011556; //stsd
	int SAMPLE_SIZE_BOX = 1937011578; //stsz
	int SAMPLE_TABLE_BOX = 1937007212; //stbl
	int SAMPLE_TO_CHUNK_BOX = 1937011555; //stsc
	int SOUND_MEDIA_HEADER_BOX = 1936549988; //smhd
	int TIME_TO_SAMPLE_BOX = 1937011827; //stts
	int TRACK_BOX = 1953653099; //trak
	int TRACK_HEADER_BOX = 1953196132; //tkhd
	int VIDEO_MEDIA_HEADER_BOX = 1986881636; //vmhd
	//sample entries
	int TEXT_METADATA_SAMPLE_ENTRY = 1835365492; //mett
	int XML_METADATA_SAMPLE_ENTRY = 1835365496; //metx
}
