// The MIT License
// 
// Copyright (c) 2010 University Corporation for Atmospheric Research
// 
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

package eu.hoefel.nujan.hdf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/**
 * A write-only buffer either in memory or on top of an open output FileChannel.
 */
final class HBuffer {

    /** Approx len of write to channel */
    static final int BLEN = 10000;

    private static final Logger logger = Logger.getLogger(HBuffer.class.getName());

    /**
     * Open output FileChannel, or null in the case of a memory-only buffer.
     */
    private FileChannel outChannel;

    /** Deflater compression level (0==none) for use with outChannel. */
    int compressionLevel;

    /** The global owning HdfFileWriter. */
    HdfFileWriter hdfFile;

    /** The in-memory buffer, or the front end to the outChannel. */
    private ByteBuffer bbuf;

    /** Used to compress when compressionLevel > 0. */
    Deflater deflater;

    /**
     * Creates a write-only buffer on top of an open output FileChannel, or, if
     * outChannel==null, in main memory.
     *
     * @param outChannel       Either an open output FileChannel, or null.
     * @param compressionLevel The Deflater compression level used with an open
     *                         output FileChannel. 0 == no compression.
     * @param hdfFile          The global owning HdfFileWriter.
     */
    HBuffer(FileChannel outChannel, // if null, just builds internal bbuf.
            int compressionLevel, HdfFileWriter hdfFile) throws HdfException {
        this.outChannel = outChannel;
        this.compressionLevel = compressionLevel;
        this.hdfFile = hdfFile;

        bbuf = ByteBuffer.allocate(BLEN);
        bbuf.order(ByteOrder.LITTLE_ENDIAN);
        if (compressionLevel > 0) {
            if (outChannel == null) {
                throw new HdfException("cannot have compressionLevel > 0 with no outChannel");
            }
            deflater = new Deflater(compressionLevel);
        }
        logger.finer(() -> String.format("HBuffer: outChannel: %s  compressionLevel: %d",
                outChannel == null ? "no" : "yes", compressionLevel));
    }

    @Override
    public String toString() {
        String res = "outChannel:";
        if (outChannel == null) {
            res += " null";
        } else {
            try {
                res += " pos: " + outChannel.position();
            } catch (IOException exc) {
                throw new UncheckedIOException(exc);
            }
        }
        res += "  bbuf: " + bbuf;
        return res;
    }

    /** Clears the in-memory buffer, but doesn't change the outChannel. */
    void clear() {
        bbuf.clear();
    }

    /** Returns the current position of the in-memory buffer. */
    int getPos() {
        return bbuf.position();
    }

    /** Sets the current position of the in-memory buffer. */
    void setPos(long pos) throws HdfException {
        if (pos < 0 || pos >= bbuf.capacity()) {
            throw new HdfException("invalid setPos");
        }
        bbuf.position((int) pos);
    }

    /**
     * Returns a subset of the bytes in the in-memory buffer: for startPos &lt;= pos
     * &lt; limPos.
     */
    byte[] getBufBytes(long startPos, long limPos) throws HdfException {
        // Allow the case of an empty buffer: startPos == limPos == 0.
        if (startPos < 0 || startPos > getPos()) {
            throw new HdfException("invalid startPos");
        }
        if (limPos < startPos || limPos > getPos()) {
            throw new HdfException("invalid limPos");
        }
        int blen = (int) (limPos - startPos);
        byte[] bytes = new byte[blen];
        for (int i = 0; i < blen; i++) {
            bytes[i] = bbuf.get((int) startPos + i);
        }
        return bytes;
    }

    /**
     * Writes the in-memory buffer to <b><tt>chan</tt><b> - a <b>different</b>
     * FileChannel than our outChannel. Must have compressionLevel == 0.
     */
    void writeChannel(FileChannel chan) throws HdfException {
        logger.finer(() -> String.format("writeChannel: bbuf: pos: %d  limit: %d  capacity: %d", getPos(), bbuf.limit(),
                bbuf.capacity()));

        if (outChannel != null) {
           throw new HdfException("two channels specified");
        }

        if (compressionLevel > 0) {
            throw new HdfException("compression not supported here");
        }

        bbuf.flip();

        try {
            chan.write(bbuf);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }

        bbuf.clear();
    }

    /**
     * Insure bbuf has at least idelta free space - if bbuf is too full, write bbuf
     * to outChannel (if outChanel != null) or expand bbuf.
     */
    private void expandBuf(int idelta) throws HdfException {
        if (getPos() + idelta > bbuf.capacity()) {
            if (outChannel == null) {
                // Expand bbuf
                int newLen = 100 + 2 * (getPos() + idelta);
                logger.finest(() -> String.format("expandBuf: expand A: getPos: %d  idelta: %d  newLen: %d", getPos(),
                        idelta, newLen));
                ByteBuffer newbuf = ByteBuffer.allocateDirect(newLen);
                newbuf.order(ByteOrder.LITTLE_ENDIAN);

                byte[] oldVals = new byte[getPos()];
                bbuf.flip();
                bbuf.get(oldVals);

                newbuf.put(oldVals);
                bbuf = newbuf;
            } else { // else we have outChannel: write it
                // Write bbuf to outChannel
                logger.finest(() -> String.format("expandBuf: write: getPos: %d  idelta: %d  compressionLevel: %d",
                        getPos(), idelta, compressionLevel));

                try {
                    if (compressionLevel > 0) {
                        writeCompressedOutput();
                    } else {
                        bbuf.flip();
                        outChannel.write(bbuf);
                    }
                } catch (IOException exc) {
                    throw new UncheckedIOException(exc);
                }

                bbuf.clear();
                // If idelta > bbuf.capacity, we still need to reallocate
                if (idelta > bbuf.capacity()) {
                    // Expand bbuf
                    int newLen = 100 + 2 * idelta;
                    logger.finest(() -> String.format("expandBuf: expand with outChannel: idelta: %d  newLen: %d",
                            idelta, newLen));
                    bbuf = ByteBuffer.allocateDirect(newLen);
                    bbuf.order(ByteOrder.LITTLE_ENDIAN);
                }
            }
        }
    }

    /**
     * Compresses bbuf contents and writes to outChannel.
     */
    private void writeCompressedOutput() {
        logger.finer(() -> {
            try {
                return """
                        writeCompressedOutput.entry:
                            bbuf: pos: %d  limit: %d  capacity: %d
                            outChannel: pos: %d
                        """.formatted(getPos(), bbuf.limit(), bbuf.capacity(), outChannel.position());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        byte[] bytes = new byte[getPos()];
        bbuf.flip();
        bbuf.get(bytes);

        deflater.setInput(bytes, 0, bbuf.position());
        byte[] compBytes = new byte[BLEN];
        ByteBuffer cbuf = ByteBuffer.wrap(compBytes);
        cbuf.order(ByteOrder.LITTLE_ENDIAN);

        while (true) {
            int compLen = deflater.deflate(compBytes);
            if (compLen == 0) {
                break;
            }

            cbuf.position(0);
            cbuf.limit(compLen);
            try {
                outChannel.write(cbuf);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        logger.finer(() -> {
            try {
                return """
                        writeCompressedOutput.exit:
                            outChannel: pos: %d
                        """.formatted(outChannel.position());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    /** Writes bbuf to outChannel, compressing if need be. */
    void flush() throws HdfException {
        if (outChannel == null) {
            throw new HdfException("cannot flush null channel");
        }

        try {
            logger.finer("flush.entry: outChannel.pos: " + outChannel.position());
            if (compressionLevel > 0) {
                deflater.finish();
                writeCompressedOutput();
                deflater.end();
            } else {
                bbuf.flip();
                outChannel.write(bbuf);
            }
            bbuf.clear();
            logger.finer("flush.exit: outChannel.pos: " + outChannel.position());
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    /**
     * Advances bbuf's position to the next multiple of bound.
     * 
     * @param msg   Unused
     * @param bound The desired multiple, such as 8.
     */
    long alignPos(String msg, long bound) throws HdfException {
        byte fillByte = 0x77;
        getPos();

        while (getPos() % bound != 0) {
            putBufByte("align fill", fillByte);
        }

        return getPos();
    }

    /**
     * Puts a single byte to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value in the low order byte.
     */
    void putBufByte(String name, int value) throws HdfException {
        expandBuf(1);
        if (value < 0 || value > 255) {
            throw new HdfException("putBufByte: invalid value: " + value);
        }
        bbuf.put((byte) value);
    }

    /**
     * Puts an array of bytes to the internal buffer.
     * 
     * @param name   debug name
     * @param values the bytes to be copied.
     */
    void putBufBytes(String name, byte[] values) throws HdfException {
        expandBuf(values.length);
        bbuf.put(values);
    }

    /**
     * Puts a single short value to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value in the low order two bytes.
     */
    void putBufShort(String name, short value) throws HdfException {
        expandBuf(2);
        bbuf.putShort(value);
    }

    /**
     * Puts a single int value to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value to be copied.
     */
    void putBufInt(String name, int value) throws HdfException {
        expandBuf(4);
        bbuf.putInt(value);
    }

    /**
     * Puts a single long value to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value to be copied.
     */
    void putBufLong(String name, long value) throws HdfException {
        expandBuf(8);
        bbuf.putLong(value);
    }

    /**
     * Puts a single float value to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value to be copied.
     */
    void putBufFloat(String name, float value) throws HdfException {
        expandBuf(4);
        bbuf.putFloat(value);
    }

    /**
     * Puts a single double value to the internal buffer.
     * 
     * @param name  debug name
     * @param value contains the value to be copied.
     */
    void putBufDouble(String name, double value) throws HdfException {
        expandBuf(8);
        bbuf.putDouble(value);
    }

    /**
     * Appends the contents of inBuf to our internal buffer.
     * 
     * @param name  debug name
     * @param inBuf The buffer to be copied.
     */
    void putBufBuf(String name, HBuffer inBuf) throws HdfException {
        int inLen = inBuf.getPos();
        expandBuf(inLen);
        byte[] inBytes = inBuf.getBufBytes(0, inLen);
        bbuf.put(inBytes);
    }
}
