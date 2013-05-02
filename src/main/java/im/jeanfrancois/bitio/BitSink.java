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

/**
 * A bit sink, where bits can be written to, which are in turn written to an
 * underlying byte sink.
 *
 * @author Jean-Francois Im
 */
public class BitSink {
    private ByteSink byteSink;
    private int currentByte = 0;
    private int currentBitCount = 0;

    /**
     * Constructs a BitSink with a given byte sink.
     *
     * @param byteSink The sink for the bytes written by this bit stream.
     */
    public BitSink(ByteSink byteSink) {
        this.byteSink = byteSink;
    }

    /**
     * Writes a single bit to the byte sink.
     *
     * @param value The bit to write
     * @throws IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeBit(final boolean value) throws IOException {
        if (value) {
            currentByte |= 1 << currentBitCount;
        }

        currentBitCount++;

        if (currentBitCount == 8) {
            byteSink.writeByte(currentByte);
            currentBitCount = 0;
            currentByte = 0;
        }
    }

    /**
     * Writes a number of zeroes to the byte sink
     *
     * @param count The number of zeroes to write
     * @throws IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeZeroes(final int count) throws IOException {
        // Do the zeroes fit in the current byte?
        if (count + currentBitCount < 8) {
            // Yes, just increment the bit pointer
            currentBitCount += count;
        } else {
            // Write the current byte
            byteSink.writeByte(currentByte);

            // Compute how many bits we have left to write
            final int bitsWrittenInLastByte = 8 - currentBitCount;
            final int bitsRemainingToWrite = count - bitsWrittenInLastByte;
            final int zeroBytesToWrite = bitsRemainingToWrite / 8;

            // Write complete zero bytes
            for (int i = 0; i < zeroBytesToWrite; ++i) {
                byteSink.writeByte(0);
            }

            // Set bits count to the number of bits remaining
            currentBitCount = bitsRemainingToWrite % 8;
            currentByte = 0;
        }
    }

    /**
     * Writes an unary-coded value to the byte sink
     *
     * @param value The value to write to the byte sink
     * @throws IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeUnary(final int value) throws IOException {
        // Can we fit the value in the current byte?
        if (value + currentBitCount + 1 < 8) {
            // Yes, just write the 1 at the proper location and increment the bit count
            currentByte |= 1 << (value + currentBitCount);
            currentBitCount += value + 1;
        } else {
            // Nope, write it normally
            writeZeroes(value);
            writeBit(true);
        }
    }

    /**
     * Writes a certain number of bits to the byte sink
     *
     * @param value   The value to write to the byte sink, must be smaller than 2<sup>numBits</sup>
     * @param numBits The number of bits to be written to the byte sink
     * @throws IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeBinary(final int value, final int numBits) throws IOException {
        // Can we fit the bits in the current byte?
        if (numBits + currentBitCount < 8) {
            // Yes, just write the value and increment the bit count
            final int maskedValue = value & ((1 << numBits) - 1);
            currentByte |= maskedValue << currentBitCount;
            currentBitCount += numBits;
        } else {
            // Write the bits that fit in the current byte
            final int bitsThatFit = 8 - currentBitCount;
            final int maskedBitsThatFit = value & ((1 << bitsThatFit) - 1);
            currentByte |= maskedBitsThatFit << currentBitCount;
            byteSink.writeByte(currentByte);

            // Compute how many bits we have left to write
            final int bitsRemainingToWrite = numBits - bitsThatFit;
            final int wholeBytesToWrite = bitsRemainingToWrite / 8;

            // Write whole bytes
            for (int i = 0; i < wholeBytesToWrite; ++i) {
                final int bitOffset = (i * 8 + bitsThatFit);
                final int valueToWrite = (value >> bitOffset) & 0xFF;
                byteSink.writeByte(valueToWrite);
            }

            // Write the remaining bits
            final int bitOffset = (wholeBytesToWrite * 8 + bitsThatFit);
            currentBitCount = bitsRemainingToWrite % 8;
            currentByte = (value >> bitOffset) & (0xFF >> (8 - currentBitCount));
        }
    }

    /**
     * Writes a Rice-coded value, which must be positive, to the byte sink. If
     * negative values are desired, the
     * {@link im.jeanfrancois.bitio.util.BitIOUtils#encodeAsZigZag(int)
     * encodeAsZigZag} method allows encoding negative values as positive
     * integers.
     *
     * @param value        The value to write, which must be positive.
     * @param numFixedBits The number of bits used for the M parameter, for example 3 would mean a value of M=2<sup>3</sup>=8.
     * @throws IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeRice(int value, int numFixedBits) throws IOException {
        int m = 1 << numFixedBits;
        int q = value / m;
        int r = value % m;

        writeUnary(q);
        writeBinary(r, numFixedBits);
    }

    /**
     * Flushes the current byte and realigns the stream to a byte boundary.
     *
     * @throws IOException If an IOException occurs while writing the current byte
     */
    public void flushCurrentByteAndRealignToByteBoundary() throws IOException {
        if (currentBitCount > 0) {
            byteSink.writeByte(currentByte);
            currentByte = 0;
            currentBitCount = 0;
        }
    }

    /**
     * Writes a complete byte to the underlying byte sink.
     *
     * @param value The value to write.
     * @throws IOException If an IOException occurs while writing the byte
     */
    public void writeByte(int value) throws IOException {
        // Are we aligned with a byte boundary?
        if (currentBitCount == 0) {
            // Yes, just write the value directly
            byteSink.writeByte(value);
        } else {
            // Write the bits that fit in the current byte
            final int bitsThatFit = 8 - currentBitCount;
            final int maskedBitsThatFit = value & ((2 << bitsThatFit) - 1);
            currentByte |= maskedBitsThatFit << currentBitCount;
            byteSink.writeByte(currentByte);

            // Write the rest of the bits in a new byte
            final int remainingBits = 8 - bitsThatFit;
            final int maskedValue = value & (((2 << remainingBits) - 1) << bitsThatFit);
            currentByte = maskedValue >> bitsThatFit;
            currentBitCount = remainingBits;
        }
    }
}
