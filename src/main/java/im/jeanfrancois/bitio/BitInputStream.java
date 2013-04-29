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

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream from which individual bits can be read from.
 *
 * @author jfim
 */
public class BitInputStream extends InputStream {
    /**
     * The bit source to which all bitwise operations are delegated.
     */
    private final BitSource bitSource;

    /**
     * The underlying input stream.
     */
    private final InputStream inputStream;

    public BitInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.bitSource = new BitSource(new InputStreamByteSource(inputStream));
    }

    @Override
    public int read() throws IOException {
        return bitSource.readByte();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    /**
     * Reads a single bit from the input stream.
     *
     * @return true, if the bit read from the input stream is '1'
     * @throws java.io.IOException If an underlying IOException occurs while reading from the stream
     */
    public boolean readBit() throws IOException {
        return bitSource.readBit();
    }

    /**
     * Reads a Rice-coded value from the input stream.
     *
     * @param numFixedBits The number of bits used for the M parameter (ie. M is always a power of 2 of value 2<sup>numFixedBits</sup>)
     * @return The Rice-coded value read from the input stream.
     * @throws java.io.IOException If an underlying IOException occurs while reading from the stream
     */
    public int readRice(int numFixedBits) throws IOException {
        return bitSource.readRice(numFixedBits);
    }

    /**
     * Reads a unary-coded value from the input stream.
     *
     * @return A unary-coded value read from the input stream
     * @throws java.io.IOException If an underlying IOException occurs while reading from the stream
     */
    public int readUnary() throws IOException {
        return bitSource.readUnary();
    }

    /**
     * Reads an entire byte from the underlying input stream.
     *
     * @return The byte read from the underlying input stream.
     * @throws java.io.IOException If reading the byte caused an IOException
     */
    public int readByte() throws IOException {
        return bitSource.readByte();
    }

    /**
     * Discards the remaining bits in the current byte, if any, and starts reading from a byte boundary.
     */
    public void realignToByteBoundary() {
        bitSource.realignToByteBoundary();
    }

    /**
     * Reads a binary value from the input stream.
     *
     * @param numBits The number of bits to read
     * @return The value for the numBits read
     * @throws java.io.IOException If an underlying IOException occurs while reading from the stream
     */
    public int readBinary(int numBits) throws IOException {
        return bitSource.readBinary(numBits);
    }
}
