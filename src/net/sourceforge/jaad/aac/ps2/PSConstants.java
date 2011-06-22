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
package net.sourceforge.jaad.aac.ps2;

interface PSConstants {

	//maximum values/lengths
	int MAX_ENVELOPES = 4;
	int MAX_IID_ICC_PARS = 34;
	int MAX_IPD_OPD_PARS = 17;
	//band numbers
	int[] ALLPASS_BANDS = {30, 50};
	int[] PAR_BANDS = {20, 34};
	//factors
	float PEAK_DECAY_FACTOR = 0.76592833836465f;
}
