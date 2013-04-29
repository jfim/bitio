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
 * TODO Document me!
 *
 * @author jfim
 */
public class TestReadWrite extends TestCase {
	@Test
	public void testAlignedReadsAndWrites() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
		for(int i = 0; i < 256; ++i) {
			bitOutputStream.writeBinary(i, 8);
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
		for(int i = 0; i < 256; ++i) {
			assertEquals(bitInputStream.readBinary(8), i);
		}
	}

	@Test
	public void testRiceCodingReadsAndWrites() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
		for(int numBits = 0; numBits < 6; ++numBits) {
			for(int i = 0; i < 127; ++i) {
				bitOutputStream.writeRice(i, numBits);
			}
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);
		for(int numBits = 0; numBits < 6; ++numBits) {
			for(int i = 0; i < 127; ++i) {
				assertEquals(bitInputStream.readRice(numBits), i);
			}
		}
	}
}
