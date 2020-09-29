//
// Copyright 2020 Western Semiconductor Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
//
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.westernsemico.util;

import java.io.*;

/** a simple Writer that indents each printed line by an adjustable amount */
public class IndentingWriter extends Writer {

    private int     indentation = 0;
    private boolean nonWhitespaceOnThisLine = false;
    private final Writer w;

    public IndentingWriter(Writer w) { this.w = w; }

    public void setIndentation(int indentation)    throws IOException { flush(); this.indentation = indentation; }
    public void adjustIndentation(int adjustment)  throws IOException { setIndentation(this.indentation+adjustment); }

    @Override public void write(int i) throws IOException {
        if (i<0) return;
        char c = (char)i;
        if (c == '\n') { w.write(i); nonWhitespaceOnThisLine = false; return; }
        if (nonWhitespaceOnThisLine) { w.write(i); return; }
        if (c == ' ') return;
        nonWhitespaceOnThisLine = true;
        for(int j=0; j<indentation; j++) w.write(' ');
        w.write(i);
    }

    @Override public void flush() throws IOException { w.flush(); }
    @Override public void close() throws IOException { w.close(); }
    public void write(char[] buf, int ofs, int len) throws IOException {
        for(int i=ofs; i<ofs+len; i++)
            write(buf[i]);
    }
}
