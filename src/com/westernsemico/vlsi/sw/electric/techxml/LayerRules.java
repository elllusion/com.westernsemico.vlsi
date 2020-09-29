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

/**
 * Encapsulates the basic DRC parameters for a layer: minimum width,
 * minimum space, minimum area, and maximum width (which can be
 * Double.MAX_VALUE)
 */
public class LayerRules {
    public final double minWidth;
    public final double minSpace;
    public final double minArea;
    public final double maxWidth;
    public LayerRules(double minWidth, double minSpace, double minArea, double maxWidth) {
        this.minWidth = minWidth;
        this.minSpace = minSpace;
        this.minArea = minArea;
        this.maxWidth = maxWidth==0 ? Double.MAX_VALUE : maxWidth;
    }
}
