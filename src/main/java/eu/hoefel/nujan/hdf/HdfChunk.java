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

import java.util.Arrays;

final class HdfChunk {

    /** start indices of this chunk in the hyperslab */
    int[] chunkStartIxs;

    /** the owning group */
    HdfGroup hdfGroup;

    /**
     * size = disk space used; may be less than the product of dims * eleSize if
     * compressed
     */
    long chunkDataSize;

    /** offset on disk */
    long chunkDataAddr;

    /**
     * 
     * @param chunkStartIxs
     * @param hdfGroup the owning group
     * @throws HdfException
     */
    HdfChunk(int[] chunkStartIxs, HdfGroup hdfGroup) throws HdfException {
        if (chunkStartIxs == null) {
            throw new HdfException("invalid chunkStartIxs");
        }
        this.chunkStartIxs = Arrays.copyOf(chunkStartIxs, chunkStartIxs.length);
        this.hdfGroup = hdfGroup;

        chunkDataSize = 0;
        chunkDataAddr = 0;
    }

    @Override
    public String toString() {
        return "  chunkStartIxs: " + HdfUtil.formatInts(chunkStartIxs) + "\n" + "  chunkDataSize: "
                + chunkDataSize + "\n" + "  chunkDataAddr: " + chunkDataAddr;
    }
}
