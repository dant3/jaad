package net.sourceforge.jaad.aac.syntax;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Random;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Simple tests for the BitStream
 * @author in-somnia
 */
public class BitStreamTest {

	private static final byte[] DATA = new byte[4];

	//fills DATA with random bytes
	@BeforeClass
	public static void setUpClass() {
		final Random r = new Random();
		r.nextBytes(DATA);
	}

	@Test
	public void testByteAlign() throws AACException {
		final BitStream bs = new BitStream(DATA);
		bs.skipBits(5);
		bs.byteAlign();
		final byte b = (byte) bs.readBits(8);
		assertEquals(DATA[1], b);
	}

	@Test
	public void testGetPosition() throws AACException {
		final BitStream bs = new BitStream(DATA);
		bs.skipBits(4);
		assertEquals(4, bs.getPosition());
		bs.skipBits(12);
		assertEquals(16, bs.getPosition());
	}

	@Test
	public void testReadCache() throws Exception {
		final BitStream bs = new BitStream(DATA);
		int x = bs.readCache(true);
		int exp, res;
		for(int i = 0; i<4; i++) {
			exp = ((int) DATA[i])&0xFF;
			res = (x>>((3-i)*8))&0xFF;
			assertEquals(exp, res);
		}

		x = bs.readCache(false);
		for(int i = 0; i<4; i++) {
			exp = ((int) DATA[i])&0xFF;
			res = (x>>((3-i)*8))&0xFF;
			assertEquals(exp, res);
		}
	}

	@Test
	public void testReadBits() throws Exception {
		final BitStream bs = new BitStream(DATA);

		int i = bs.readBits(5);
		int expected = (DATA[0]>>3)&31;
		assertEquals(expected, i);

		i = bs.readBits(7);
		expected = ((DATA[0]&7)<<4)|((DATA[1]>>4)&15);
		assertEquals(expected, i);
	}

	@Test
	public void testReadBit() throws Exception {
		final BitStream bs = new BitStream(DATA);
		int exp;
		for(int i = 0; i<DATA.length; i++) {
			for(int j = 0; j<8; j++) {
				exp = (DATA[i]>>(7-j))&1;
				assertEquals(exp, bs.readBit());
			}
		}
	}

	@Test
	public void testPeekBits() throws Exception {
		final BitStream bs = new BitStream(DATA);

		int i = bs.peekBits(5);
		int expected = (DATA[0]>>3)&31;
		assertEquals(expected, i);

		bs.skipBits(2);
		i = bs.peekBits(5);
		expected = (DATA[0]>>1)&31;
		assertEquals(expected, i);

		bs.skipBits(4);
		i = bs.peekBits(7);
		expected = ((DATA[0]&3)<<5)|((DATA[1]>>3)&31);
		assertEquals(expected, i);
	}

	@Test
	public void testPeekBit() throws Exception {
		final BitStream bs = new BitStream(DATA);

		int i = bs.peekBit();
		int expected = (DATA[0]>>7)&1;
		assertEquals(expected, i);

		bs.skipBits(4);
		i = bs.peekBit();
		expected = (DATA[0]>>3)&1;
		assertEquals(expected, i);
	}

	@Test
	public void testSkipBits() throws Exception {
		final BitStream bs = new BitStream(DATA);

		bs.skipBits(5);
		assertEquals(5, bs.getPosition());
		bs.skipBits(7);
		assertEquals(12, bs.getPosition());
	}

	@Test
	public void testSkipBit() throws Exception {
		final BitStream bs = new BitStream(DATA);

		for(int i = 0; i<DATA.length*8; i++) {
			bs.skipBit();
			assertEquals(i+1, bs.getPosition());
		}
	}
}
