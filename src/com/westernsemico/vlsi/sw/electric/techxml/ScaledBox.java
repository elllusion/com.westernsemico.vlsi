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
import com.westernsemico.util.*;
import java.io.*;

/**
 * Encapsulates a <box><lambdaBox/></box> in an Electric Technology
 * XML File.  This gives the size of something relative to some larger
 * thing it is part of.  Specifically, for each axis (x and y) the
 * size of this thing along that axis equals
 * add+magnification*other where other is the size of the "larger
 * thing" on that same axis and magnification is a scalar.  It's
 * actually slightly more general than this; you can specify the
 * magnification in each direction (right/left edge for x, top/bot
 * edge for y) separately, but I can't imagine what this is useful
 * for.
 */
public class ScaledBox {
    public final Box add;
    public final Box mult;
    public ScaledBox(double size) { this(size, size); }
    public ScaledBox(Box add, Box mult) { this.add = add; this.mult = mult; }
    public ScaledBox(Box add, double mag_x, double mag_y) { this(add, new Box(2.0*mag_x,2.0*mag_y)); }
    public ScaledBox(Box add, double magnification) { this(add, magnification, magnification); }
    public ScaledBox(Box add) { this(add, 1.0); }
    public ScaledBox(double width, double height, double magnification) { this(new Box(width, height), magnification); }
    public ScaledBox(double width, double height, double mag_x, double mag_y) { this(new Box(width, height), mag_x, mag_y); }
    public ScaledBox(double width, double height) { this(width, height, 1.0); }
    public ScaledBox shift(double x, double y) { return new ScaledBox(add.shift(x, y), mult); }
    public void dump(IndentingPrintWriter pw) throws IOException {
        if (mult.equals(new Box())) {
            pw.println("<box>");
        } else {
            pw.println("<box "+mult.toString()+">");
        }
        pw.adjustIndentation(4);
        pw.println("<lambdaBox"+add.toString()+"/>");
        pw.adjustIndentation(-4);
        pw.println("</box>");
    }
}
