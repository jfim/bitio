BitIO
=====

A fast, bit-oriented stream library. Many bit-oriented stream libraries
available through copy-paste Java files on the Internet are naive
implementations that loop over a method that puts a single bit at a time,
which is *really* inefficient. BitIO uses bit operators to provide bulk
operations on bits, which is much faster.

It also has utilities for Rice coding (also known as Golomb-Rice coding)
and ZigZag encoding.

    import im.jeanfrancois.bitio;

    BitOutputStream bitOutputStream = new BitOutputStream(outputStream);

    // Write the value 3, encoded over two bits
    bitOutputStream.writeBinary(3, 2);

    // Write two zeros
    bitOutputStream.writeZeroes(1);
    bitOutputStream.writeBit(false);

    // Write 2, unary encoded (ie. 001b)
    bitOutputStream.writeUnary(2);

    // Realign to be on a byte boundary
    bitOutputStream.flushCurrentByteAndRealignToByteBoundary();

This will have written the value 67 (0100 0011) to the underlying output
stream, where the xxxx xx11 part is from the first writeBinary, the
xxxx 00xx part is from the writeZeros and writeBit calls and the x100
xxxx part is from the writeUnary method.

    // Write 42 using Rice coding and M=16 (2<sup>4<sup>), so that
    // q = 2, r = 10
    bitOutputStream.writeRice(42, 4);

    // Close the stream and flushes the partially written byte
    bitOutputStream.close();

This will have written the value 84 (x101 0100) to the stream. As the byte is
not completely filled, it will not be written unless either
flushCurrentByteAndRealignToByteBoundary() or close() is called.
