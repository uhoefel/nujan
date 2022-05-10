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

/** Puts the License in a static String. */
final class License {

    /** Private constructor to hide the public one. */
    private License() {
        throw new IllegalStateException("License file");
    }

//    /** The license. */
//    public static final String LICENSE = 
//            """
//            The MIT License
//            
//            Copyright (c) 2010 University Corporation for Atmospheric Research
//            Permission is hereby granted, free of charge, to any person
//            obtaining a copy of this software and associated documentation
//            files (the "Software"), to deal in the Software without
//            restriction, including without limitation the rights to use,
//            copy, modify, merge, publish, distribute, sublicense, and/or sell
//            copies of the Software, and to permit persons to whom the
//            Software is furnished to do so, subject to the following conditions:
//            
//            The above copyright notice and this permission notice shall be
//            included in all copies or substantial portions of the Software.
//            
//            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//            EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//            OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//            NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//            HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//            WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
//            FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//            OTHER DEALINGS IN THE SOFTWARE.
//            """;
    
    /** The license. */
    public static final String LICENSE = 
            "The MIT License"
            +"\n\n"
            +"Copyright (c) 2010 University Corporation for Atmospheric Research"+"\n"
            +"Permission is hereby granted, free of charge, to any person"+"\n"
            +"obtaining a copy of this software and associated documentation"+"\n"
            +"files (the \"Software\"), to deal in the Software without"+"\n"
            +"restriction, including without limitation the rights to use,"+"\n"
            +"copy, modify, merge, publish, distribute, sublicense, and/or sell"+"\n"
            +"copies of the Software, and to permit persons to whom the"+"\n"
            +"Software is furnished to do so, subject to the following conditions:"+"\n"
            +"\n\n"
            +"The above copyright notice and this permission notice shall be"+"\n"
            +"included in all copies or substantial portions of the Software."+"\n"
            +"\n\n"
            +"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,"+"\n"
            +"EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES"+"\n"
            +"OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND"+"\n"
            +"NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT"+"\n"
            +"HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,"+"\n"
            +"WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING "+"\n"
            +"FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR" + "\n"
            +"OTHER DEALINGS IN THE SOFTWARE.";
}
