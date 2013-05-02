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

import java.io.EOFException;
import java.io.IOException;

/**
 * A bit-oriented input stream.
 *
 * @author Jean-Francois Im
 */
public class BitSource {
    private ByteSource byteSource;
    private int currentByte;
    private int currentBitPosition = 8;

    /**
     * Constructs a BitSource with a given source.
     *
     * @param byteSource The underlying byte source to read from.
     */
    public BitSource(ByteSource byteSource) {
        this.byteSource = byteSource;
    }

    /**
     * Reads a single bit from the input stream.
     *
     * @return true, if the bit read from the input stream is '1'
     * @throws IOException If an underlying IOException occurs while reading from the stream
     */
    public boolean readBit() throws IOException {
        if (currentBitPosition == 8) {
            currentBitPosition = 0;
            currentByte = byteSource.readByte();

            if (currentByte == -1)
                throw new EOFException();
        }

        final boolean returnValue = (currentByte & (1 << currentBitPosition)) != 0;

        currentBitPosition++;

        return returnValue;
    }

    /**
     * Reads a unary-coded value from the input stream.
     *
     * @return A unary-coded value read from the input stream
     * @throws IOException If an underlying IOException occurs while reading from the stream
     */
    public int readUnary() throws IOException {
        int value = 0;

        while (!readBit())
            value++;

        return value;
    }

    /**
     * Reads a Rice-coded value from the input stream.
     *
     * @param numFixedBits The number of bits used for the M parameter (ie. M is always a power of 2 of value 2<sup>numFixedBits</sup>)
     * @return The Rice-coded value read from the input stream.
     * @throws IOException If an underlying IOException occurs while reading from the stream
     */
    public int readRice(int numFixedBits) throws IOException {
        int q = readUnary();
        int r = readBinary(numFixedBits);
        int m = 1 << numFixedBits;

        return (q << numFixedBits) + r;
    }

    /**
     * Reads a binary value from the input stream.
     *
     * @param numBits The number of bits to read
     * @return The value for the numBits read
     * @throws IOException If an underlying IOException occurs while reading from the stream
     */
    public int readBinary(int numBits) throws IOException {
        // Is the current value completely contained within the current byte?
        if (currentBitPosition + numBits <= 8) {
            // Yes, just read the value
            final int value = (currentByte >> currentBitPosition) & ((1 << numBits) - 1);
            currentBitPosition += numBits;
            return value;
        } else {
            int value = 0;

            // Read the bits remaining in the current byte
            final int bitsLeftInCurrentByte = 8 - currentBitPosition;
            value |= (currentByte >> currentBitPosition) & ((1 << bitsLeftInCurrentByte) - 1);

            // Compute the number of bytes to read
            final int bitsRemainingToRead = numBits - bitsLeftInCurrentByte;
            final int bytesToRead = bitsRemainingToRead / 8;

            // Read whole bytes in
            for (int i = 0; i < bytesToRead; ++i) {
                final int offset = (i * 8 + bitsLeftInCurrentByte);
                value |= byteSource.readByte() << offset;
            }

            // Read remaining bits
            final int trailingBitCount = bitsRemainingToRead % 8;
            if (trailingBitCount != 0) {
                currentByte = byteSource.readByte();
                value |= (currentByte & ((1 << trailingBitCount) - 1)) << (bytesToRead * 8 + bitsLeftInCurrentByte);
                currentBitPosition = trailingBitCount;
            } else {
                currentBitPosition = 8;
            }

            return value;
        }
    }

    /**
     * Discards the remaining bits in the current byte, if any, and starts reading from a byte boundary.
     */
    public void realignToByteBoundary() {
        if (currentBitPosition != 0)
            currentBitPosition = 8;
    }

    /**
     * Resets the source state, forcing it to read a byte on the next operation.
     */
    public void resetState() {
        currentBitPosition = 8;
    }

    /**
     * Reads an entire byte from the underlying input stream.
     *
     * @return The byte read from the underlying input stream.
     * @throws IOException If reading the byte caused an IOException
     */
    public int readByte() throws IOException {
        // Are we byte aligned?
        if (currentBitPosition % 8 == 0) {
            // Do we need to read in a byte?
            if (currentBitPosition == 8) {
                // Yes, read one and return it
                return byteSource.readByte();
            } else {
                // No, return the byte we have and set the current bit position so we read a byte the next time we read.
                currentBitPosition = 8;
                return currentByte;
            }
        } else {
            // Read remaining bits in current byte
            final int bitsInCurrentByte = 8 - currentBitPosition;
            final int bitsInNextByte = currentBitPosition;
            int value = (currentByte >> currentBitPosition) & ((1 << bitsInCurrentByte) - 1);

            // Read in a new byte and the remaining bits that we need to read
            currentByte = byteSource.readByte();
            value |= (currentByte & ((1 << bitsInNextByte) - 1)) << bitsInCurrentByte;

            return value;
        }
    }
}
