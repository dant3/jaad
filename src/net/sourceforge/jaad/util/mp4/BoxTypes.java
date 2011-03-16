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
package net.sourceforge.jaad.util.mp4;

public interface BoxTypes {

	int AUDIO_SAMPLE_ENTRY_BOX = 1836069985; //mp4a
	int CHUNK_OFFSET_BOX = 1937007471; //stco
	int CHUNK_LARGE_OFFSET_BOX = 1668232756; //co64
	int ESD_BOX = 1702061171; //esds
	int EXTENDED_BOX = 1970628964; //uuid
	int FILE_TYPE_BOX = 1718909296; //ftyp
	int MEDIA_BOX = 1835297121; //mdia
	int MEDIA_DATA_BOX = 1835295092; //mdat
	int MEDIA_HEADER_BOX = 1835296868; //mdhd
	int MEDIA_INFORMATION_BOX = 1835626086; //minf
	int MOVIE_BOX = 1836019574; //moov
	int MOVIE_HEADER_BOX = 1836476516; //mvhd
	int SAMPLE_DESCRIPTION_BOX = 1937011556; //stsd
	int SAMPLE_SIZE_BOX = 1937011578; //stsz
	int SAMPLE_TABLE_BOX = 1937007212; //stbl
	int SAMPLE_TO_CHUNK_BOX = 1937011555; //stsc
	int SOUND_MEDIA_HEADER_BOX = 1936549988; //smhd
	int TIME_TO_SAMPLE_BOX = 1937011827; //stts
	int TRACK_BOX = 1953653099; //trak
	int TRACK_HEADER_BOX = 1953196132; //tkhd
}
