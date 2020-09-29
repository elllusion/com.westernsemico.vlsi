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
package com.westernsemico.vlsi.io.gds;

/** represents a gds layer identified by a pair of integers for major/minor; the "name" is only for debugging+display */
public class GDSLayer {
    public final String name;
    public final int major;
    public final int minor;
    public GDSLayer(int major, int minor) { this(null, major, minor); }
    public GDSLayer(String name, int major, int minor) {
        this.name = name;
        this.major = major;
        this.minor = minor;
    }
    public boolean equals(Object other) {
        if (!(other instanceof GDSLayer)) return false;
        GDSLayer go = (GDSLayer)other;
        return go.major == major && go.minor == minor;
    }
    public int hashCode() { return major ^ minor; }
    public String toString() { return name+"("+major+"/"+minor+")"; }
}
