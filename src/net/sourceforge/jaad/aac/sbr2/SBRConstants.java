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

interface SBRConstants {

	//frame classes
	int FIXFIX = 0;
	int FIXVAR = 1;
	int VARFIX = 2;
	int VARVAR = 3;
	//indizes for frequency tables
	int HIGH = 1;
	int LOW = 0;
	int RATE = 2;
	int TIME_SLOTS = 16; //TODO: 15 for 960-sample frames
	int TIME_SLOTS_RATE = TIME_SLOTS*RATE;
	int NOISE_FLOOR_OFFSET = 6;
	int T_HF_GEN = 8;
	int T_HF_ADJ = 2;
	//extension ids
	int EXTENSION_ID_PS = 2;
	//helper constants
	double LOG2 = 0.6931471805599453;
	int[] PAN_OFFSETS = {24, 12};
}
