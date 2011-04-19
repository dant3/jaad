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
package net.sourceforge.jaad.aac.invquant;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for standard inverse quantization
 * @author in-somnia
 */
public class InvQuantTest implements IQTable {

	private static final float TOLERANCE = 1e-1f;
	private static final int TEST_ROUNDS = 1000;
	private static final float FOUR_THIRD = 4.0f/3.0f;
	private static final Random RAND = new Random();

	@Test
	public void testComputeGain() {
		int x;
		float y, exp;
		for(int i = 0; i<TEST_ROUNDS; i++) {
			x = Math.round(RAND.nextFloat()*255);
			exp = (float) Math.pow(2, 0.25*(x-100));
			y = InvQuant.computeGain(x);
			assertEquals(exp, y, TOLERANCE);
		}
	}

	@Test
	public void testInvQuant() {
		int x;
		float y, exp;
		for(int i = 0; i<TEST_ROUNDS; i++) {
			x = Math.round(RAND.nextFloat()*8190);
			exp = Math.signum(x)*(float) Math.pow(x, FOUR_THIRD);
			y = computeInvQuant(x);
			assertEquals(exp, y, TOLERANCE);
		}
	}

	//copy of the method, since it is private in InvQuant
	private static float computeInvQuant(int q) {
		float d;
		if(q<0) d = -IQ_TABLE[-q];
		else d = IQ_TABLE[q];
		return d;
	}
}
