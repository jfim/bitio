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
 * Byte sink that writes into an output stream.
 *
 * @author jfim
 */
public class OutputStreamByteSink implements ByteSink {
    private final OutputStream outputStream;

    public OutputStreamByteSink(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void writeByte(int byteValue) throws IOException {
        outputStream.write(byteValue);
    }
}
