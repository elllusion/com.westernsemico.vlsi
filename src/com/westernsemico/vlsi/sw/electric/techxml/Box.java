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

/** encapsulates a <box> element in an Electric Technology XML File */
public class Box {
    public final double klx;
    public final double kly;
    public final double khx;
    public final double khy;
    public Box() { this(-1, -1, 1, 1); }
    public Box(double klx, double kly, double khx, double khy) {
        this.klx = klx;
        this.kly = kly;
        this.khx = khx;
        this.khy = khy;
    }
    /** origin-centered square */
    public Box(double widthHeight) { this(widthHeight, widthHeight); }
    /** origin-centered box */
    public Box(double width, double height) {
        this(-1.0*width/2.0,
             -1.0*height/2.0,
             width/2.0,
             height/2.0);
    }
    public Box grow(double expansion) { return grow(expansion, expansion); }
    public Box grow(double horizontalEdgeExpansion,
                    double verticalEdgeExpansion) {
        return new Box(klx - horizontalEdgeExpansion,
                       kly -   verticalEdgeExpansion,
                       khx + horizontalEdgeExpansion,
                       khy +   verticalEdgeExpansion);
    }
    public Box swapAxes() { return new Box(kly, klx, khy, khx); }
    public Box shift(double x, double y) { return new Box(klx + x, kly + y, khx + x, khy + y); }
    public int hashCode() { return Double.hashCode(khx) ^ Double.hashCode(khy) ^ Double.hashCode(klx) ^ Double.hashCode(kly); }
    public boolean equals(Object other) { Box b = (Box)other; return b.khx==khx && b.khy==khy && b.klx==klx && b.kly==kly; }
    public double getWidth() { return khx - klx; }
    public double getHeight() { return khy - kly; }
    public String toString() {
        return
            (" klx='"+klx+"'")+
            (" kly='"+kly+"'")+
            (" khx='"+khx+"'")+
            (" khy='"+khy+"'");
    }
}
