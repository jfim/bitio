/*
 BitIO: A library for bit-oriented input/output.
 Copyright (C) 2009-2011 Jean-Francois Im

 This file is part of BitIO.

 BitIO is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 BitIO is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with BitIO.  If not, see <http://www.gnu.org/licenses/>.
 */

package im.jeanfrancois.bitio;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * TODO Document me! :3
 *
 * @author jfim
 */
public class TestReadWrite extends TestCase {
	public void testAlignedReadsAndWrites() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
		for(int i = 0; i < 256; ++i) {
			bitOutputStream.writeBinary(i, 8);
		}
        bitOutputStream.close();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
		for(int i = 0; i < 256; ++i) {
			assertEquals(bitInputStream.readBinary(8), i);
		}
        bitInputStream.close();
	}

    public void testVariousReadsAndWritesExhaustive() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int numBits = 0; numBits < 10; ++numBits) {
            int maxValue = (1 << numBits) - 1;
            for(int value = 0; value < maxValue; ++value) {
                bitOutputStream.writeBinary(value, numBits);
            }
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int numBits = 0; numBits < 10; ++numBits) {
            int maxValue = (1 << numBits) - 1;
            for(int value = 0; value < maxValue; ++value) {
                assertEquals(bitInputStream.readBinary(numBits), value);
            }
        }
        bitInputStream.close();
    }

    public void testWriteNegativeNumbers() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int numBits = 0; numBits < 10; ++numBits) {
            for(int value = -1024; value < 1024; ++value) {
                bitOutputStream.writeBinary(value, numBits);
            }
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int numBits = 0; numBits < 10; ++numBits) {
            for(int value = -1024; value < 1024; ++value) {
                final int expectedValue = (0x03FF >> (10 - numBits)) & value;
                assertEquals(bitInputStream.readBinary(numBits), expectedValue);
            }
        }
        bitInputStream.close();
    }

    public void testWriteValueRange() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int value = -1024; value < 1024; ++value) {
            bitOutputStream.writeBinary(value, 32);
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int value = -1024; value < 1024; ++value) {
            assertEquals(bitInputStream.readBinary(32), value);
        }
        bitInputStream.close();
    }

    public void testWriteMinMax() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int i = 0; i < 50; ++i) {
            if (i % 2 == 0) {
                bitOutputStream.writeBinary(Integer.MIN_VALUE, 32);
            } else {
                bitOutputStream.writeBinary(Integer.MAX_VALUE, 32);
            }
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int i = 0; i < 50; ++i) {
            if (i % 2 == 0) {
                assertEquals(bitInputStream.readBinary(32), Integer.MIN_VALUE);
            } else {
                assertEquals(bitInputStream.readBinary(32), Integer.MAX_VALUE);
            }
        }
        bitInputStream.close();
    }

	public void testRiceCodingReadsAndWrites() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
		for(int numBits = 0; numBits < 6; ++numBits) {
			for(int i = 0; i < 127; ++i) {
				bitOutputStream.writeRice(i, numBits);
			}
		}
        bitOutputStream.close();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
		for(int numBits = 0; numBits < 6; ++numBits) {
			for(int i = 0; i < 127; ++i) {
				assertEquals(bitInputStream.readRice(numBits), i);
			}
		}
        bitInputStream.close();
	}

    public void testMisalignedByteWrites() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int i = 0; i < 256; ++i) {
            for(int numBits = 0; numBits < 16; ++numBits) {
                bitOutputStream.writeZeroes(numBits);
                bitOutputStream.write(i);
            }
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int i = 0; i < 256; ++i) {
            for(int numBits = 0; numBits < 16; ++numBits) {
                assertEquals(0, bitInputStream.readBinary(numBits));
                assertEquals(i, bitInputStream.read());
            }
        }
        bitInputStream.close();
    }

    public void testRealignedByteWrites() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        for(int i = 0; i < 256; ++i) {
            for(int numBits = 0; numBits < 16; ++numBits) {
                bitOutputStream.writeZeroes(numBits);
                bitOutputStream.flushCurrentByteAndRealignToByteBoundary();
                bitOutputStream.write(i);
            }
        }
        bitOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
        for(int i = 0; i < 256; ++i) {
            for(int numBits = 0; numBits < 16; ++numBits) {
                assertEquals(0, bitInputStream.readBinary(numBits));
                bitInputStream.realignToByteBoundary();
                assertEquals(i, bitInputStream.read());
            }
        }
        bitInputStream.close();
    }

    public void testBinaryFormat() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1);
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);

        // Write the value 3, encoded over two bits
        bitOutputStream.writeBinary(3, 2);

        // Write two zeros
        bitOutputStream.writeZeroes(1);
        bitOutputStream.writeBit(false);

        // Write 2, unary encoded (ie. 001b)
        bitOutputStream.writeUnary(2);

        // Realign to be on a byte boundary
        bitOutputStream.flushCurrentByteAndRealignToByteBoundary();

        // Write 42 using Rice coding and M=16 (2<sup>4<sup>), so that
        // q = 2, r = 10 (ie. x101 0100 = 84)
        bitOutputStream.writeRice(42, 4);

        // Realign to be on a byte boundary
        bitOutputStream.flushCurrentByteAndRealignToByteBoundary();

        bitOutputStream.close();

        assertEquals(67, byteArrayOutputStream.toByteArray()[0]);
        assertEquals(84, byteArrayOutputStream.toByteArray()[1]);
    }
}
