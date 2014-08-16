package net.sourceforge.jaad.aac;

import java.util.Random;
import static org.junit.Assert.*;

public class TestUtils {

	private static final float TOLERANCE = 1e-5f;
	private static final Random RAND = new Random();

	public static float[] generateRandomVector(int len) {
		final float[] d = new float[len];
		for(int i = 0; i<len; i++) {
			d[i] = RAND.nextFloat();
		}
		return d;
	}

	public static float[][] generateRandomVectorC(int len) {
		final float[][] d = new float[len][2];
		for(int i = 0; i<len; i++) {
			d[i][0] = RAND.nextFloat();
			d[i][1] = RAND.nextFloat();
		}
		return d;
	}

	public static void print(float[] d, String name) {
		System.out.println(name+" ("+d.length+"):");
		for(int i = 0; i<d.length; i++) {
			System.out.println(i+": "+d[i]);
		}
		System.out.println("===========================");
	}

	public static void print(float[][] d, String name) {
		System.out.println(name+" ("+d.length+"):");
		for(int i = 0; i<d.length; i++) {
			System.out.println(i+": ("+d[i][0]+", "+d[i][1]+")");
		}
		System.out.println("===========================");
	}

	public static void compare(float[] d1, float[] d2) {
		assertEquals(d1.length, d2.length);
		for(int i = 0; i<d1.length; i++) {
			assertEquals(d1[i], d2[i], TOLERANCE);
		}
	}

	public static void compare(float[][] d1, float[][] d2) {
		assertEquals(d1.length, d2.length);
		for(int i = 0; i<d1.length; i++) {
			assertEquals(d1[i][0], d2[i][0], TOLERANCE);
			assertEquals(d1[i][1], d2[i][1], TOLERANCE);
		}
	}
}
