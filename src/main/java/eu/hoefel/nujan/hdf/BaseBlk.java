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

/**
 * Represents a metadata structure (block).
 * <p>
 * Extended by BtreeNode GlobalHeap HdfFileWriter HdfGroup LocalHeap MsgBase
 * SymbolTable SymTabEntry
 * <p>
 * The subclass must override formatBuf to format the structure to the output
 * buffer.
 */
abstract class BaseBlk {

    /**
     * The offset of this BaseBlk in HdfFileWriter.mainBuf.
     */
    long blkPosition;

    /**
     * For debug: the name of the BaseBlk, such as "HdfGroup" or "MsgAttribute".
     */
    String blkName;

    /**
     * The global owning HdfFileWriter.
     */
    HdfFileWriter hdfFile;

    /**
     * @param blkName For debug: the name of the BaseBlk, such as "HdfGroup" or
     *                "MsgAttribute".
     * @param hdfFile The global owning HdfFileWriter.
     */
    BaseBlk(String blkName, HdfFileWriter hdfFile) {
        this.blkName = blkName;
        this.hdfFile = hdfFile;
    }

    @Override
    public String toString() {
        return String.format("  blkName: %s  pos: 0x%x", blkName, blkPosition);
    }

    /**
     * Formats this individual BaseBlk to the output buffer fmtBuf.
     * 
     * @param formatPass
     *                   <ul>
     *                   <li>1: Initial formatting to determine the formatted
     *                   length. In HdfGroup we add msgs to hdrMsgList.
     *                   <li>2: Final formatting.
     *                   </ul>
     * @param fmtBuf     output buffer
     * @throws HdfException if a HDF5 specific exception occurs
     */
    abstract void formatBuf(int formatPass, HBuffer fmtBuf) throws HdfException;

    /**
     * Aligns fmtBuf position to multiple of 8 and sets our blkPosition to the new
     * position. Prints debug message for formatBuf entry. Should be called first
     * thing in formatBuf() in every class extending BaseBlk.
     */
    void setFormatEntry(int formatPass, boolean useAlign, HBuffer fmtBuf) throws HdfException {
        hdfFile.indent++;

        fmtBuf.alignPos("setFormatEntry for " + blkName, 8);
        blkPosition = fmtBuf.getPos();
    }
}
