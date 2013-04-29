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
import java.io.OutputStream;

/**
 * Output stream to which individual bits can be written.
 *
 * @author jfim
 */
public class BitOutputStream extends OutputStream {
    private final OutputStream outputStream;
    private final BitSink bitSink;

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        bitSink = new BitSink(new OutputStreamByteSink(outputStream));
    }

    @Override
    public void write(int value) throws IOException {
        bitSink.writeByte(value);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        bitSink.flushCurrentByteAndRealignToByteBoundary();
        outputStream.close();
    }

    /**
     * Writes a single bit to the output stream.
     *
     * @param value The bit to write
     * @throws java.io.IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeBit(boolean value) throws IOException {
        bitSink.writeBit(value);
    }

    /**
     * Writes a number of zeroes to the output stream
     *
     * @param count The number of zeroes to write
     * @throws java.io.IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeZeroes(int count) throws IOException {
        bitSink.writeZeroes(count);
    }

    /**
     * Flushes the current byte and realigns the stream to a byte boundary.
     *
     * @throws java.io.IOException If an IOException occurs while writing the current byte
     */
    public void flushCurrentByteAndRealignToByteBoundary() throws IOException {
        bitSink.flushCurrentByteAndRealignToByteBoundary();
    }

    /**
     * Writes a Rice-coded value to the output stream.
     *
     * @param value        The value to write, which must be positive.
     * @param numFixedBits The number of bits used for the M parameter, for example 3 would mean a value of M=2<sup>3</sup>=8.
     * @throws java.io.IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeRice(int value, int numFixedBits) throws IOException {
        bitSink.writeRice(value, numFixedBits);
    }

    /**
     * Writes a certain number of bits to the output stream
     *
     * @param value   The value to write to the output stream, must be smaller than 2<sup>numBits</sup>
     * @param numBits The number of bits to be written to the output stream
     * @throws java.io.IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeBinary(int value, int numBits) throws IOException {
        bitSink.writeBinary(value, numBits);
    }

    /**
     * Writes a complete byte to the underlying output stream.
     *
     * @param value The value to write.
     * @throws java.io.IOException If an IOException occurs while writing the byte
     */
    public void writeByte(int value) throws IOException {
        bitSink.writeByte(value);
    }

    /**
     * Writes an unary-coded value to the output stream
     *
     * @param value The value to write to the output stream
     * @throws java.io.IOException If an underlying IOException occurs while writing to the stream
     */
    public void writeUnary(int value) throws IOException {
        bitSink.writeUnary(value);
    }
}
