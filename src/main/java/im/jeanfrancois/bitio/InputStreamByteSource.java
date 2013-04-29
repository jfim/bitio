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
 * Byte source that reads from an input stream.
 *
 * @author jfim
 */
public class InputStreamByteSource implements ByteSource {
    private final InputStream inputStream;

    public InputStreamByteSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int readByte() throws IOException {
        return inputStream.read();
    }
}
