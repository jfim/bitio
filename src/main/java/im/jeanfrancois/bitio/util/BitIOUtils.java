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

package im.jeanfrancois.bitio.util;

/**
 * Various utilities useful when working with bit streams.
 *
 * @author jfim
 */
public class BitIOUtils {
    private BitIOUtils() {}

    /**
     * Encodes a value using ZigZag encoding (0 => 0, -1 => 1, 1 => 2, -2 => 3, 2 => 4), so that the resulting value is always positive.
     *
     * @param value The value to encode
     * @return The ZigZag encoded value
     */
    public static int encodeAsZigZag(final int value) {
        return (value << 1) ^ (value >> 31);
    }

    /**
     * Decodes a previously ZigZag encoded value.
     *
     * @param value The value to decode
     * @return The decoded value
     */
    public static int decodeZigZag(final int value) {
        return ((value << 31) >> 31) ^ (value >>> 1);
    }
}
