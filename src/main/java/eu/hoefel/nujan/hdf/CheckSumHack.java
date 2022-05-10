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
 * Implements the samed checksum hack as used by the HDF5 C code. There is so
 * much well published, well established theory for strong checksums. Why use a
 * hack like this?
 * <p>
 * For the hack details, see the HDF5 file: H5checksum.c
 */
final class CheckSumHack {

    int a;
    int b;
    int c;

    static final int rotate(int x, int len) {
        return (x << len) ^ (x >>> (32-len));
    }

    void mixStd() {
        a -= c;
        a ^= rotate(c, 4);
        c += b;
        b -= a;
        b ^= rotate(a, 6);
        a += c;
        c -= b;
        c ^= rotate(b, 8);
        b += a;
        a -= c;
        a ^= rotate(c, 16);
        c += b;
        b -= a;
        b ^= rotate(a, 19);
        a += c;
        c -= b;
        c ^= rotate(b, 4);
        b += a;
    }

    void mixFinal() {
        c ^= b;
        c -= rotate(b, 14);
        a ^= c;
        a -= rotate(c, 11);
        b ^= a;
        b -= rotate(a, 25);
        c ^= b;
        c -= rotate(b, 16);
        a ^= c;
        a -= rotate(c, 4);
        b ^= a;
        b -= rotate(a, 14);

        c ^= b;
        c -= rotate(b, 24);
    }

    /**
     * Returns the checksum of bytes. For the hack details, see the HDF5 file:
     * H5checksum.c
     */
    int calcHackSum(byte[] bytes) {
        int bugs = 0;
        if (bugs >= 1)
            prtf("calcHackSum: len: %d", bytes.length);
        if (bugs >= 5) {
            for (int i = 0; i < bytes.length; i++) {
                prtf("  ii: %d  byte: %d  '%c'", i, 0xff & bytes[i], 0xff & bytes[i]);
            }
        }

        int initval = 0;
        a = 0xdeadbeef + bytes.length + initval;
        b = a;
        c = a;

        int ix = 0;
        while (ix < bytes.length-12) {
            a += (0xff & bytes[ix + 0]);
            a += (0xff & bytes[ix + 1]) << 8;
            a += (0xff & bytes[ix + 2]) << 16;
            a += (0xff & bytes[ix + 3]) << 24;
            ix += 4;

            b += (0xff & bytes[ix + 0]);
            b += (0xff & bytes[ix + 1]) << 8;
            b += (0xff & bytes[ix + 2]) << 16;
            b += (0xff & bytes[ix + 3]) << 24;
            ix += 4;

            c += (0xff & bytes[ix + 0]);
            c += (0xff & bytes[ix + 1]) << 8;
            c += (0xff & bytes[ix + 2]) << 16;
            c += (0xff & bytes[ix + 3]) << 24;
            ix += 4;

            if (bugs >= 5) {
                prtf("    apremix: %d", a);
                prtf("    bpremix: %d", b);
                prtf("    cpremix: %d", c);
            }

            mixStd();
            if (bugs >= 5) {
                prtf("    amix: %d", a);
                prtf("    bmix: %d", b);
                prtf("    cmix: %d", c);
            }
        }

        if (bugs >= 5) {
            prtf("    final rem length: %d", bytes.length - ix);
        }

        // all case statements fall through
        switch (bytes.length-ix) {
            case 12: c += (0xff & bytes[ix + 11]) << 24;
            case 11: c += (0xff & bytes[ix + 10]) << 16;
            case 10: c += (0xff & bytes[ix + 9]) << 8;
            case 9:  c += (0xff & bytes[ix + 8]);

            case 8: b += (0xff & bytes[ix + 7]) << 24;
            case 7: b += (0xff & bytes[ix + 6]) << 16;
            case 6: b += (0xff & bytes[ix + 5]) << 8;
            case 5: b += (0xff & bytes[ix + 4]);

            case 4: a += (0xff & bytes[ix + 3]) << 24;
            case 3: a += (0xff & bytes[ix + 2]) << 16;
            case 2: a += (0xff & bytes[ix + 1]) << 8;
            case 1: a += (0xff & bytes[ix + 0]);
        }

        if (bugs >= 5) {
            prtf("    aprefinal: %d", a);
            prtf("    bprefinal: %d", b);
            prtf("    cprefinal: %d", c);
        }

        mixFinal();

        if (bugs >= 5) {
            prtf("    afinal: %d", a);
            prtf("    bfinal: %d", b);
            prtf("    cfinal: %d", c);
        }

        return c;
    }

    static void prtf(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
    }
}
