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
package com.westernsemico.vlsi.sw.electric.techxml;
import java.io.*;

/**
 * Java class which encapsulates the <outlined> and <opaqueColor>
 * subelements of the <layer> element in an Electric Technology XML
 * File.  Basically this is a 24-bit color plus a Pattern (see that
 * class) plus a boolean indicating whether or not the border outline
 * should be drawn as a solid line.  Border outlines other than "none"
 * or "solid line" (e.g. dotted, dashed, etc) are not yet supported.
 */
public class RenderingStyle {
    public final Pattern pattern;
    public final String border;
    public final int r;
    public final int g;
    public final int b;
    public RenderingStyle(Pattern pattern, boolean border, int r, int g, int b) { this(pattern, border?"PAT_S":"NOPAT", r, g, b); }
    public RenderingStyle(Pattern pattern, String border, int r, int g, int b) {
        this.pattern = pattern;
        this.border = border;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static RenderingStyle rs(Pattern pattern) { return rs(pattern, false); }
    public static RenderingStyle rs(Pattern pattern, boolean border, int r, int g, int b) { return new RenderingStyle(pattern, border, r, g, b); }
    public static RenderingStyle rs(Pattern pattern, String border, int r, int g, int b) { return new RenderingStyle(pattern, border, r, g, b); }
    public static RenderingStyle rs(Pattern pattern, int r, int g, int b) { return rs(pattern, false, r, g, b); }
    public static RenderingStyle rs(Pattern pattern, boolean border) { return rs(pattern, border, 100, 100, 100); }
    public static RenderingStyle rs(int r, int g, int b) { return rs(Pattern.solid, r, g, b); }
}
