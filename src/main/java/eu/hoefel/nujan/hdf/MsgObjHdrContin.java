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
 * HDF5 message type 16: object header continuation - not used in this package
 * <p>
 * Extends abstract MsgBase, so we must implement formatMsgCore - see the
 * documentation for class {@link MsgBase}.
 */
final class MsgObjHdrContin extends MsgBase {

    long continAddr; // position of continuation
    long continLen; // len of continuation

    /**
     * 
     * @param hdfGroup the owning group
     * @param hdfFile
     */
    MsgObjHdrContin(HdfGroup hdfGroup, HdfFileWriter hdfFile) {
        super(TP_OBJ_HDR_CONTIN, hdfGroup, hdfFile);
    }

    @Override
    public String toString() {
        String res = super.toString();
        res += "  continAddr: " + continAddr;
        res += "  continLen: " + continLen;
        return res;
    }

    // Format everything after the message header
    @Override
    void formatMsgCore(int formatPass, HBuffer fmtBuf) throws HdfException {
        throw new HdfException("We do not use continuations");
    }
}
