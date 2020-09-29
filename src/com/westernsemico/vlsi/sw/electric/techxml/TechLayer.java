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
import com.westernsemico.vlsi.io.gds.GDSLayer;
import com.westernsemico.vlsi.sw.electric.techxml.*;
import static com.westernsemico.vlsi.sw.electric.techxml.Pattern.*;
import com.westernsemico.vlsi.sw.electric.techxml.RenderingStyle;
import static com.westernsemico.vlsi.sw.electric.techxml.RenderingStyle.rs;
import com.westernsemico.util.*;
import java.util.*;
import java.io.*;

/** A Technology Layer -- encapsulates roughly one masking step and the layer(s) for it (geometry, LVS resistor, dummy, etc) */
public class TechLayer {

    /** the technology this layer belongs to */
    public final Tech tech;

    /** the name used by Electric for this layer */
    public final String name;

    /** the GDS major/minor index on which to emit geometry */
    public final GDSLayer  gdsGeom;

    /** the GDS major/minor index on which to emit pins */
    public final GDSLayer  gdsPin;
    
    /** if this layer has a corresponding dummy layer (i.e. for metal fill), it goes here */
    public final TechLayer layer_dummy;

    /** if this layer has a corresponding resistor layer, it goes here */
    public final TechLayer layer_res;

    /** if this layer has a corresponding fill-block layer, it goes here */
    public final TechLayer layer_block;

    /** the basic rules for this layer (min width/space/area, maxwidth) */
    public final LayerRules layerRules;

    /** a description of this layer */
    public final String description;

    /** true if this is a polysilicon layer */
    public final boolean isPoly;

    /** true if this is a diffusion layer */
    public final boolean isDiff;

    /** if this is a metal layer, then metalNumber is its ordinal; otherwise metalNumber is -1 */
    public final int metalNumber;

    /** true if this is a signal layer which participates in networks (i.e. poly, diff, metal) rather than a nonelectrical layer (implants, lvs layers, etc) */
    public       boolean isSignal;

    /** true if this is a metal layer */
    public final boolean isMetal;

    /** the default rendering style (color, outline, and pattern) to use if the user does not override this in their preferences */
    public final RenderingStyle rs;

    /** the "function" of this layer -- see Electric Technology XML documentation for details */
    public final String fun;

    /** the 3D height of the bottom surface of this layer */
    public double height3D = 0.0;

    /** the 3D thickness of this layer */
    public double thick3D  = 0.0;

    /** every layer has a corresponding "pure layer primitive node" with one port; this field indicates which arcs are allowed to connect to that port */
    private final HashSet<TechLayer> pureLayerNodeConnections = new HashSet<TechLayer>();

    /** the set of other layers whose geometry are forbidden to overlap the geometry of this layer */
    private HashSet<TechLayer> forbidden = new HashSet<TechLayer>();

    /** the minimum spacings to geometry on other layers, regardless of connectivity -- i.e. "notch" rules */
    private HashMap<TechLayer,Double> interLayerSpacings = new HashMap<TechLayer,Double>();

    /** the minimum spacings to geometry on other layers UNLESS those other layers are on the same net as this one -- i.e. "short circuit" rules */
    private HashMap<TechLayer,Double> interLayerSpacingsUnconnected = new HashMap<TechLayer,Double>();

    public TechLayer(Tech tech, String name, GDSLayer gdsGeom, GDSLayer gdsPin, String description) {
        this(tech, name, gdsGeom, gdsPin, null, description); }
    public TechLayer(Tech tech, String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules layerRules, String description) {
        this(tech, name, gdsGeom, gdsPin, layerRules, null, description); }
    public TechLayer(Tech tech, String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules layerRules, RenderingStyle explicitRS, String description) {
        this(tech, name, gdsGeom, gdsPin, layerRules, explicitRS, description, null, null, null); }
    public TechLayer(Tech tech, String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules layerRules, RenderingStyle explicitRS, String description,
                  GDSLayer gds_res, GDSLayer gds_dummy, GDSLayer gds_block) {
        this(tech, name, gdsGeom, gdsPin, layerRules, explicitRS, description, gds_res, gds_dummy, gds_block,
             name.equals("Aluminum") ? tech.numLayersPolyOrMetal()-1 :
             !(name.equals("Aluminum") ||
               (name.startsWith("M") && Character.isDigit(name.charAt(1)) &&
                (name.length()==2 ||
                 (name.length()==3 && Character.isDigit(name.charAt(2))))))
             ? -1
             : Integer.parseInt(name.substring(1)));
    }
    public TechLayer(Tech tech, String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules layerRules, RenderingStyle explicitRS, String description,
                  GDSLayer gds_res, GDSLayer gds_dummy, GDSLayer gds_block, int metalNumber) {
        this.tech = tech;
        RenderingStyle dummy_rs = null;
        name = name.replace(' ', '-');
        name = name.replace('.', '-');
        name = name.replace(':', '-');
        this.name = name;
        this.gdsGeom = gdsGeom;
        this.gdsPin = gdsPin;
        this.layerRules = layerRules;
        this.description = description;
        this.isMetal = metalNumber > -1;
        this.metalNumber = metalNumber;
        this.isPoly = name.toLowerCase().startsWith("poly") && !name.toLowerCase().startsWith("polymide");
        this.isDiff =
            (name.toLowerCase().startsWith("diff") && !name.toLowerCase().startsWith("diff-1v8") && !name.toLowerCase().startsWith("diff-2v5")) ||
            (name.toLowerCase().equals("tap"));
        this.isSignal = isPoly || isDiff || isMetal;

        String fun = "UNKNOWN"; /* FIXME */
        if (name.toLowerCase().equals("pwell") ||
            name.toLowerCase().equals("nwell") ||
            name.toLowerCase().equals("dnwell") ||
            name.toLowerCase().equals("well-n") ||
            name.toLowerCase().equals("well-p") ||
            name.toLowerCase().equals("nwell-deep")
            )
            fun = "SUBSTRATE"; // for implants generator

        String iname = name;
        if (iname.startsWith("ZZZ-")) iname = iname.substring(4);
            
        RenderingStyle rs = rs(sparse);
        if (name.startsWith("Electric-")) {
            rs = rs(empty, 0, 0, 0);
        } else if (isMetal) {
            fun = "METAL"+(metalNumber-tech.lowestMetalLayerOrdinal()+1);
            switch(metalNumber-tech.lowestMetalLayerOrdinal()+1) {
            case 1: rs = rs(solid,   72, 189, 178); break;
            case 2: rs = rs(solid,  224,  95, 255); break;
            case 3: rs = rs(solid,  168, 171,  14); break;
            case 4: rs = rs(solid,   44, 222,  45); break;
            case 5: rs = rs(solid,    0, 255, 204); break;
            case 6: rs = rs(solid,  153, 153, 255); break;
            case 7: rs = rs(solid,  204,   0, 204); break;
                        
            case 8: rs = rs(solid,   72, 189, 178); break;
            case 9: rs = rs(solid,  224,  95, 255); break;
            case 10:rs = rs(solid,  168, 171,  14); break;
            case 11:rs = rs(solid,   44, 222,  45); break;
            }
            dummy_rs = new RenderingStyle(very_sparse, true, rs.r/2, rs.g/2, rs.b/2);
        } else if (name.toLowerCase().startsWith("cont") || name.toLowerCase().startsWith("licon1") || name.toLowerCase().startsWith("mcon")) {
            fun = "CONTACT1";
            rs = rs(solid,
                    //quarters[0],
                    false, 0x20,0x20,0x20);
        } else if (name.toLowerCase().equals("via")) {
            int which = 1;
            rs = rs(//which % 2 == 0 ? backslash_mixed : slash_mixed,
                    empty,
                    //solid,
                    //quarters[which],
                    "PAT_T2",
                    255,255,255);
        } else if (name.toLowerCase().startsWith("via") && name.length()>3) {
            fun = "CONTACT1";
            String s = name.charAt(3)+"";
            if (s.startsWith("-")) s = s.substring(1);
            int which = 0;
            try {
                which = Integer.parseInt(s) % 4;
            } catch (NumberFormatException nfe) { }
            rs = rs(//which % 2 == 0 ? backslash_mixed : slash_mixed,
                    empty,
                    //solid,
                    //quarters[which],
                    "PAT_T2",
                    255,255,255);
        } else if (name.toLowerCase().equals("gate"))   { rs = rs(solid, 255, 155, 192); fun = "POLY1";
        } else if (name.toLowerCase().equals("gate-dummy")) { rs = rs(dense, true, 255, 155, 192); fun = "POLY1";
        } else if (isDiff)                              { rs = rs(solid,   0, 135,  51); fun ="DIFF";
        } else if (isPoly)                              { rs = rs(solid, 255, 155, 192); fun = "POLY1";
        } else if (name.toLowerCase().equals("nwell"))  { rs = rs(slash,     139, 99, 46); fun = "WELLN";
        } else if (name.toLowerCase().equals("pwell"))  { rs = rs(backslash, 139, 99, 46); fun = "WELLP";
        } else if (name.toLowerCase().equals("well-n")) { rs = rs(slash,     139, 99, 46); fun = "WELLN";
        } else if (name.toLowerCase().equals("well-p")) { rs = rs(backslash, 139, 99, 46); fun = "WELLP";
        } else if (iname.equals("Select-PAct"))         { rs = rs(slash,     255, 153, 0); fun = "IMPLANTP";
        } else if (iname.equals("Select-NAct"))         { rs = rs(backslash, 102, 255, 51); fun = "IMPLANTN";
        } else if (iname.equals("Select-PTap"))         { rs = rs(empty, true, 102, 255, 51); fun = "IMPLANTP";
        } else if (iname.equals("Select-NTap"))         { rs = rs(empty, true, 255, 153, 0); fun = "IMPLANTN";

        } else if (iname.equals("Vt-N-CORE"))  { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-CORE"))  { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";

        } else if (iname.equals("Vt-ELVT"))     { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-ELVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-ELVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
                
        } else if (iname.equals("Vt-ULVT"))     { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-ULVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-ULVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
                
        } else if (iname.equals("Vt-LVT"))     { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-LVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-LVT"))   { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-Gate"))  { rs = rs(very_sparse, 102, 255, 51); fun = "IMPLANTN";
                
        } else if (iname.equals("Vt-SVT"))     { rs = rs(very_sparse, 255, 255,  0); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-SVT"))   { rs = rs(very_sparse, 255, 255,  0); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-SVT"))   { rs = rs(very_sparse, 255, 255,  0); fun = "IMPLANTP";

        } else if (iname.equals("Vt-HVT"))     { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-HVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-HVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";

        } else if (iname.equals("Vt-UHVT"))     { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-UHVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-UHVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";

        } else if (iname.equals("Vt-EHVT"))     { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";
        } else if (iname.equals("Vt-N-EHVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTN";
        } else if (iname.equals("Vt-P-EHVT"))   { rs = rs(very_sparse, 255,   0,  0); fun = "IMPLANTP";
        }
        this.fun = fun;
        this.rs  = explicitRS==null ? rs : explicitRS;

        tech.techLayers.put(name, this);

        this.layer_res      = gds_res==null   ? null : new TechLayer(tech, name+"-Res",   gds_res,   null, "Resistor for "+name);
        this.layer_dummy    = gds_dummy==null ? null : new TechLayer(tech, name+"-Dummy", gds_dummy, null, null, dummy_rs, "Dummy for "+name);
        this.layer_block    = gds_block==null ? null : new TechLayer(tech, name+"-Block", gds_block, null, "Block for "+name);
    }

    public String toString() { return name; }
    public void forbidden(TechLayer other) { forbidden.add(other); }
    public void addSpacingRule(TechLayer other, double distance) { interLayerSpacings.put(other, distance); }
    public void addUnconnectedSpacingRule(TechLayer other, double distance) { interLayerSpacingsUnconnected.put(other, distance); }
    public void addPureLayerNodeConnection(TechLayer layer) { pureLayerNodeConnections.add(layer); }

    public void dump(IndentingPrintWriter pw) throws IOException {
        double minWidth  = (layerRules == null ? 0.0 : layerRules.minWidth);
        double halfWidth = minWidth / 2.0;
        // FIXME: need the "Nothing" layer

        String extraFun = ""; /* FIXME */
        if (name.startsWith("Dummy-")) extraFun = "extraFun=\"nonelectrical\""; /* FIXME */
        if (name.startsWith("Diff-1v8")) extraFun = "extraFun=\"nonelectrical\""; /* FIXME */

        pw.println("    <layer name=\""+name+"\" fun=\""+fun+"\" "+extraFun+">");
        pw.adjustIndentation(4);
        pw.println("        <opaqueColor r=\""+rs.r+"\" g=\""+rs.g+"\" b=\""+rs.b+"\"/>");
        pw.println("        <patternedOnDisplay>true</patternedOnDisplay> <!-- dummy value -->");
        pw.println("        <patternedOnPrinter>true</patternedOnPrinter> <!-- dummy value -->");
        rs.pattern.dump(pw);
        //if (rs.border) pw.println("        <outlined>PAT_S</outlined>");
        //else           pw.println("        <outlined>NOPAT</outlined>");
        pw.println("        <outlined>"+rs.border+"</outlined>");
        pw.println("        <opacity>1.0</opacity>");
        pw.println("        <foreground>true</foreground> <!-- dummy value -->");
        pw.println("        <display3D thick=\""+thick3D+"\" height=\""+height3D+"\" mode=\"NONE\" factor=\"0.0\"/>");
        pw.println("        <cifLayer cif=\"COG\"/> <!-- dummy value -->");
        pw.println("        <pureLayerNode name=\""+name+"\" port=\"port\">");
        pw.println("            <lambda>"+minWidth+"</lambda>");
        for(TechLayer layer : pureLayerNodeConnections)
            pw.println("            <portArc>"+layer+"</portArc>");
        if (isSignal)
            pw.println("            <portArc>"+name+"</portArc>");
        pw.println("        </pureLayerNode>");
        pw.adjustIndentation(-4);
        pw.println("    </layer>");
        pw.println();
    }

    public void dumpArcs(IndentingPrintWriter pw) throws IOException {
        if (!isSignal) return;
        double minWidth  = (layerRules == null ? 0.0 : layerRules.minWidth);
        double halfWidth = minWidth / 2.0;
        pw.println("    <arcProto name=\""+name+"\" fun=\""+fun+"\">");
        pw.println("        <wipable/>");
        pw.println("        <extended>true</extended>");
        pw.println("        <fixedAngle>true</fixedAngle>");
        pw.println("        <angleIncrement>45</angleIncrement>");
        pw.println("        <antennaRatio>400.0</antennaRatio> <!-- dummy value -->");
        pw.println("        <arcLayer layer=\""+name+"\" style=\"FILLED\">");
        pw.println("            <lambda>"+halfWidth+"</lambda>");
        pw.println("        </arcLayer>");
        pw.println("    </arcProto>");
    }

    public void dumpPins(IndentingPrintWriter pw) throws IOException {
        if (!isSignal) return;
        double minWidth  = (layerRules == null ? 0.0 : layerRules.minWidth);
        double halfWidth = minWidth / 2.0;
        pw.println("    <primitiveNodeGroup>");
        pw.println("        <primitiveNode name=\""+name+"-Pin\" fun=\"PIN\"/>");
        pw.println("        <shrinkArcs/>");
        pw.println("        <nodeBase><box><lambdaBox klx=\"-"+halfWidth+"\" khx=\""+halfWidth+"\"");
        pw.println("                                  kly=\"-"+halfWidth+"\" khy=\""+halfWidth+"\"/>");
        pw.println("                 </box></nodeBase>");
        pw.println("        <nodeLayer layer=\""+name+"\" style=\"CROSSED\">");
        pw.println("            <box>");
        pw.println("                <lambdaBox klx=\"-"+halfWidth+"\" khx=\""+halfWidth+"\"");
        pw.println("                           kly=\"-"+halfWidth+"\" khy=\""+halfWidth+"\"/>");
        pw.println("            </box>");
        pw.println("        </nodeLayer>");
        pw.println("        <primitivePort name=\"port\">");
        pw.println("            <portAngle primary=\"0\" range=\"180\"/>");
        pw.println("            <portTopology>0</portTopology>");
        pw.println("            <box klx=\"0.0\" khx=\"0.0\" kly=\"0.0\" khy=\"0.0\">");
        pw.println("                <lambdaBox klx=\"0.0\" khx=\"0.0\" kly=\"0.0\" khy=\"0.0\"/>");
        pw.println("            </box>");
        pw.println("            <portArc>"+name+"</portArc>");
        for(TechLayer layer : pureLayerNodeConnections)
            pw.println("            <portArc>"+layer+"</portArc>");
        pw.println("        </primitivePort>");
        pw.println("    </primitiveNodeGroup>");
        pw.println();
    }

    public void dumpGdsMapping(IndentingPrintWriter pw) throws IOException {
        if (gdsGeom == null) return;
        String gdsmap = gdsGeom!=null ? (gdsGeom.major+"/"+gdsGeom.minor) : "";
        if (gdsPin != null) gdsmap += (gdsmap.equals("")?"":",")+(gdsPin.major+'/'+gdsPin.minor)+"p";
        pw.println("<layerGds layer='"+name+"' gds='"+gdsmap+"'/>");
    }
    
    public void dumpRules(IndentingPrintWriter pw) throws IOException {
        if (layerRules != null) {
            if (layerRules.minWidth > 0)
                pw.println("<LayerRule ruleName='"+this+".MINWIDTH' "+
                           "layerName='"+this+"' type='MINWID' when='ALL' value='"+(layerRules.minWidth)+"'/>");
            if (layerRules.minSpace > 0)
                pw.println("<LayersRule ruleName='"+this+".SPACING' "+
                           "layerNames='{"+this+","+this+"}' type='UCONSPA' when='ALL' value='"+(layerRules.minSpace)+"'/>");
        }
        for(TechLayer otherLayer : interLayerSpacings.keySet()) {
            pw.println("<LayersRule ruleName='"+this+".TO."+otherLayer+".SPACING' "+
                       "layerNames='{"+this+","+otherLayer+"}' "+
                       "type='SPACING' when='ALL' value='"+(interLayerSpacings.get(otherLayer))+"'/>");
        }
        for(TechLayer otherLayer : interLayerSpacingsUnconnected.keySet()) {
            pw.println("<LayersRule ruleName='"+this+".TO."+otherLayer+".USPACING' "+
                       "layerNames='{"+this+","+otherLayer+"}' "+
                       "type='UCONSPA' when='ALL' value='"+(interLayerSpacingsUnconnected.get(otherLayer))+"'/>");
        }
        for(TechLayer otherLayer : forbidden) {
            pw.println("<LayersRule ruleName='"+this+".AND."+otherLayer+".FORBIDDEN' "+
                       "layerNames='{"+this+","+otherLayer+"}' "+
                       "type='SPACING' when='ALL' value='0'/>");
        }
        // FIXME: minarea
        // FIXME: maxwidth
    }
}
