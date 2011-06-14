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
package net.sourceforge.jaad.aac.sbr2;

interface SBRTables {

	int[] MFT_START_MIN = {7, 7, 10, 11, 12, 16, 16, 17, 24};
	int[] MFT_STOP_MIN = {13, 15, 20, 21, 23, 32, 32, 35, 48};
	int[] MFT_SF_OFFSETS = {5, 5, 4, 4, 4, 3, 2, 1, 0};
	int[][] MFT_START_OFFSETS = {
		{-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7}, //16000
		{-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13}, //22050
		{-5, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16}, //24000
		{-6, -4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16}, //32000
		{-4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20}, //44100-64000
		{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20, 24} //>64000
	};
	int[][] MFT_STOP_OFFSETS = {
		{2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 37, 44, 51},
		{2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 36, 42, 49},
		{2, 4, 6, 9, 11, 14, 17, 21, 25, 29, 34, 39, 44},
		{2, 4, 6, 9, 11, 14, 17, 21, 24, 28, 33, 38, 43},
		{2, 4, 6, 9, 11, 14, 17, 20, 24, 28, 32, 36, 41},
		{2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32},
		{2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32},
		{2, 3, 5, 7, 9, 11, 13, 16, 18, 21, 23, 26, 29},
		{1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 15, 16}
	};
	int[] MFT_INPUT1 = {12, 10, 8};
	float[] MFT_INPUT2 = {1.0f, 1.3f};
	float[] LIM_BANDS_PER_OCTAVE_POW = {
		1.32715174233856803909f, //2^(0.49/1.2)
		1.18509277094158210129f, //2^(0.49/2)
		1.11987160404675912501f //2^(0.49/3)
	};
}
