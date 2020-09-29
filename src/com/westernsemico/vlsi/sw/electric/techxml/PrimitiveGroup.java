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
import java.util.*;
import java.io.*;

/** Encapsulates a <primitivePrimitiveGroup> element in an Electric Technology XML File */
public class PrimitiveGroup {
    ArrayList<Primitive> primitives = new ArrayList<Primitive>();
    public PrimitiveGroup(Tech tech) { tech.primitiveGroups.add(this); }
    public void dump(IndentingPrintWriter pw) throws IOException {
        for(Primitive primitive : primitives) {
            pw.println("<primitiveNodeGroup>");
            pw.adjustIndentation(4);
            primitive.dump(pw);
            pw.adjustIndentation(-4);
            pw.println("</primitiveNodeGroup>");
        }
    }
}
