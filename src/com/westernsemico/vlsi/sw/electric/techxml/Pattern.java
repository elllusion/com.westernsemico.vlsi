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

/** An Electric Technology XML "pattern", which is basically a 16x16 monochrome bitmap. */
public class Pattern {

    /** the 16x16 bits of the bitmap */
    private boolean[][] bits = new boolean[16][];

    /** create a pattern from 16 Java strings, each of which must be 16 characters long, where each character is 'X' or ' ' */
    public Pattern(String[] lines) {
        if (lines.length != 16) throw new RuntimeException("patterns must have 16 rows");
        for(int i=0; i<lines.length; i++) {
            if (lines[i].length() != 16) throw new RuntimeException("patterns must have 16 columns");
            bits[i] = new boolean[16];
            for(int j=0; j<16; j++)
                bits[i][j] = lines[i].charAt(j) != ' ';
        }
    }

    /** dump the XML representation of the pattern */
    public void dump(PrintWriter pw) {
        for(int row=0; row<bits.length; row++) {
            pw.print("        <pattern>");
            for(int col=0; col<bits[row].length; col++)
                pw.print(bits[row][col] ? 'X' : ' ');
            pw.println("</pattern>");
        }
    }


    // static declarations for useful patterns //////////////////////////////////////////////////////////////////////////////
    
    public static final Pattern empty =
        new Pattern(new String[] {
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
            });

    public static final Pattern solid =
        new Pattern(new String[] {
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXX",
            });

    public static final Pattern pad =
        new Pattern(new String[] {
                "XXXX            ",
                "X   X           ",
                "X   X           ",
                "XXX             ",
                "X     X         ",
                "X    X X        ",
                "X   X   X       ",
                "    XXXXX       ",
                "    X   X       ",
                "    X   X       ",
                "          XXXX  ",
                "          X   X ",
                "          X    X",
                "          X   X ",
                "          XXXX  ",
                "                "
            });

    public static final Pattern ivd =
        new Pattern(new String[] {
                "XXXXX           ",
                "  X             ",
                "  X             ",
                "  X             ",
                "XXXXX           ",
                "                ",
                "    X     X     ",
                "     X   X      ",
                "      X X       ",
                "       X        ",
                "          XXXX  ",
                "          X   X ",
                "          X    X",
                "          X   X ",
                "          XXXX  ",
                "                "
            });

    public static final Pattern ld =
        new Pattern(new String[] {
                "X               ",
                "X               ",
                "X               ",
                "X               ",
                "X               ",
                "X               ",
                "XXXXX           ",
                "                ",
                "          XXXX  ",
                "          X   X ",
                "          X    X",
                "          X    X",
                "          X    X",
                "          X   X ",
                "          XXXX  ",
                "                " });

    public static final Pattern three_point_three =
        new Pattern(new String[] {
                "XXXXX           ",
                "     X          ",
                "     X          ",
                " XXXX           ",
                "     X          ",
                "     X          ",
                "XXXXX           ",
                "      XX        ",
                "      XX        ",
                "         XXXXX  ",
                "              X ",
                "              X ",
                "          XXXX  ",
                "              X ",
                "              X ",
                "         XXXXX  ",
            });


    public static final Pattern dense =
        new Pattern(new String[] {
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X" });

    public static final Pattern sparse =
        new Pattern(new String[] {
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                ",
                "X   X   X   X   ",
                "                " });

    public static final Pattern very_sparse =
        new Pattern(new String[] {
                "X       X       ",
                "                ",
                "    X       X   ",
                "                ",
                "X       X       ",
                "                ",
                "    X       X   ",
                "                ",
                "X       X       ",
                "                ",
                "    X       X   ",
                "                ",
                "X       X       ",
                "                ",
                "    X       X   ",
                "                " });

    public static final Pattern carats =
        new Pattern(new String[] {
                "   X       X    ",
                "  X X     X X   ",
                " X   X   X   X  ",
                "X     X X     X ",
                "       X       X",
                "                ",
                "                ",
                "                ",
                "   X       X    ",
                "  X X     X X   ",
                " X   X   X   X  ",
                "X     X X     X ",
                "       X       X",
                "                ",
                "                ",
                "                "
            });


    public static final Pattern slash_dense =
        new Pattern(new String[] {
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
            });

    public static final Pattern backslash_dense =
        new Pattern(new String[] {
                "X   X   X   X   ",
                " X   X   X   X  ",
                "  X   X   X   X ",
                "   X   X   X   X",
                "X   X   X   X   ",
                " X   X   X   X  ",
                "  X   X   X   X ",
                "   X   X   X   X",
                "X   X   X   X   ",
                " X   X   X   X  ",
                "  X   X   X   X ",
                "   X   X   X   X",
                "X   X   X   X   ",
                " X   X   X   X  ",
                "  X   X   X   X ",
                "   X   X   X   X",
            });

    public static final Pattern dot_circle =
        new Pattern(new String[] {
                "                ",
                "      XX      XX",
                " X  X    X  X   ",
                "      XX      XX",
                "                ",
                "  XX      XX    ",
                "X    X  X    X  ",
                "  XX      XX    ",
                "                ",
                "      XX      XX",
                " X  X    X  X   ",
                "      XX      XX",
                "                ",
                "  XX      XX    ",
                "X    X  X    X  ",
                "  XX      XX    ",
            });

    public static final Pattern slash =
        new Pattern(new String[] {
                "  X       X     ",
                "                ",
                "X       X       ",
                "                ",
                "      X       X ",
                "                ",
                "    X       X   ",
                "                ",
                "  X       X     ",
                "                ",
                "X       X       ",
                "                ",
                "      X       X ",
                "                ",
                "    X       X   ",
                "                ",
            });

    public static final Pattern backslash =
        new Pattern(new String[] {
                "   X       X    ",
                "                ",
                "     X       X  ",
                "                ",
                "       X       X",
                "                ",
                " X       X      ",
                "                ",
                "   X       X    ",
                "                ",
                "     X       X  ",
                "                ",
                "       X       X",
                "                ",
                " X       X      ",
                "                ",
            });

    public static final Pattern slash_thick =
        new Pattern(new String[] {
                " XXX     XXX    ",
                "                ",
                "XX     XXX     X",
                "                ",
                "     XXX     XXX",
                "                ",
                "   XXX     XXX  ",
                "                ",
                " XXX     XXX    ",
                "                ",
                "XX     XXX     X",
                "                ",
                "     XXX     XXX",
                "                ",
                "   XXX     XXX  ",
                "                ",
            });

    public static final Pattern backslash_thick =
        new Pattern(new String[] {
                "  XXX     XXX   ",
                "                ",
                "    XXX     XXX ",
                "                ",
                "X     XXX     XX",
                "                ",
                "XXX     XXX     ",
                "                ",
                "  XXX     XXX   ",
                "                ",
                "    XXX     XXX ",
                "                ",
                "X     XXX     XX",
                "                ",
                "XXX     XXX     ",
                "                ",
            });


    public static final Pattern backslash_mixed =
        new Pattern(new String[] {
                "X   X   X   X   ",
                "     X       X  ",
                "  X   X   X   X ",
                "       X       X",
                "X   X   X   X   ",
                " X       X      ",
                "  X   X   X   X ",
                "   X       X   X",
                "X   X   X   X   ",
                "     X       X  ",
                "  X   X   X   X ",
                "       X       X",
                "X   X   X   X   ",
                " X       X      ",
                "  X   X   X   X ",
                "   X       X   X",
            });

    public static final Pattern slash_mixed =
        new Pattern(new String[] {
                "  X   X   X   X ",
                " X       X      ",
                "X   X   X   X   ",
                "       X       X",
                "  X   X   X   X ",
                "     X       X  ",
                "X   X   X   X   ",
                "   X       X   X",
                "  X   X   X     ",
                " X       X   X  ",
                "X   X   X       ",
                "       X   X   X",
                "  X   X       X ",
                "     X   X   X  ",
                "X   X       X   ",
                "   X   X   X   X",
            });

    public static final Pattern fill_50pct =
        new Pattern(new String[] {
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
                "X X X X X X X X ",
                " X X X X X X X X",
            });

    public static final Pattern[] quarters =
        new Pattern[] {
        new Pattern(new String[] {
                "X   X   XXXXXXXX",
                "   X   XXXXXXXXX",
                "  X   X XXXXXXXX",
                " X   X  XXXXXXXX",
                "X   X   XXXXXXXX",
                "   X   XXXXXXXXX",
                "  X   X XXXXXXXX",
                " X   X  XXXXXXXX",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
            }),
        new Pattern(new String[] {
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   XXXXXXXX",
                "   X   XXXXXXXXX",
                "  X   X XXXXXXXX",
                " X   X  XXXXXXXX",
                "X   X   XXXXXXXX",
                "   X   XXXXXXXXX",
                "  X   X XXXXXXXX",
                " X   X  XXXXXXXX",
            }),
        new Pattern(new String[] {
                "XXXXXXXXX   X   ",
                "XXXXXXXX   X   X",
                "XXXXXXXX  X   X ",
                "XXXXXXXX X   X  ",
                "XXXXXXXXX   X   ",
                "XXXXXXXX   X   X",
                "XXXXXXXX  X   X ",
                "XXXXXXXX X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
            }),
        new Pattern(new String[] {
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "X   X   X   X   ",
                "   X   X   X   X",
                "  X   X   X   X ",
                " X   X   X   X  ",
                "XXXXXXXXX   X   ",
                "XXXXXXXX   X   X",
                "XXXXXXXX  X   X ",
                "XXXXXXXX X   X  ",
                "XXXXXXXXX   X   ",
                "XXXXXXXX   X   X",
                "XXXXXXXX  X   X ",
                "XXXXXXXX X   X  ",
            })
    };

}
