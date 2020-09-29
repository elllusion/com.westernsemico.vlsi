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
import static com.westernsemico.vlsi.sw.electric.techxml.Pattern.*;
import com.westernsemico.vlsi.sw.electric.techxml.RenderingStyle;
import static com.westernsemico.vlsi.sw.electric.techxml.RenderingStyle.rs;
import com.westernsemico.util.*;
import java.util.*;
import java.io.*;

/** Encapsulates an Electric Technology and emits the XML file which describes it */
public abstract class Tech {

    /** the default grid alignment in Electric "units" (which are usally microns) */
    public final double gridAlignmentInUnits;

    /** TechLayers for this Tech, indexed by name */
    final HashMap<String,TechLayer> techLayers = new HashMap<String,TechLayer>();

    /** all primitive node groups for this technology */
    final ArrayList<PrimitiveGroup> primitiveGroups = new ArrayList<PrimitiveGroup>();

    /** a many-to-one map from GDS major/minor to TechLayer (a TechLayer can have geometry/pin/res/dum gdslayers associated with it) */
    private final HashMap<GDSLayer,TechLayer> gdsLayerToTechLayer = new HashMap<GDSLayer,TechLayer>();

    public Tech() { this(0); }
    
    public Tech(double gridAlignmentInUnits) {
        this.gridAlignmentInUnits = gridAlignmentInUnits;
    }

    /** the foundry-recommended core voltage */
    public abstract double getFoundryRecommendedVdd();

    /** override this if you have a "metal 0" */
    public int lowestMetalLayerOrdinal() { return 1; }

    /** override this and return the number of metal layers plus the number of poly layers */
    public abstract int numLayersPolyOrMetal();
    
    /** returns the wire capacitance of the given layer to the substrate in fF/lambda; poly is lowestMetalLayerOrdinal()-1 */
    public abstract double getCapacitanceFemtoFaradsPerNm(int layer);

    /** returns the wire sheet resistence of the given layer in ohms per square; poly is lowestMetalLayerOrdinal()-1 */
    public abstract double getResistanceOhmsPerSquare(int layer);

    /** used to calculate the load presented by a wire in X-units (the smallest manufacturable transistor is X=1) */
    public double getNfetGateCapacitanceFemtoFaradsForX1() { throw new RuntimeException("not implemented"); }

    /** used to calculate the load presented by a wire in X-units (the smallest manufacturable transistor is X=1) */
    public double getNfetDrainCapacitanceFemtoFaradsForX1() { throw new RuntimeException("not implemented"); }

    public String getTechDescription() { return toString(); }

    /** the name of the foundry that manufactures this technology */
    public abstract String getFoundryName();

    /** override this if you want to print a header comment in the Technology XML file */
    protected void header(IndentingPrintWriter pw) throws IOException { }
    
    private void registerGdsLayerToTechLayer(GDSLayer g, TechLayer t) {
        if (g==null) return;
        TechLayer old = gdsLayerToTechLayer.get(g);
        if (old != null)
            throw new RuntimeException("GDS layer " + g + " already associated to TechLayer " + old + "; cannot associate it with " + t);
        gdsLayerToTechLayer.put(g, t);
    }

    /** Retrieve a TechLayer by name */
    public TechLayer getLayer(String name) {
        name = name.replace(':', '-');
        TechLayer ret = techLayers.get(name);
        if (ret == null) throw new Error("could not find layer '"+name+"'");
        return ret;
    }

    /** dump the Technology XML file */
    protected void dump(IndentingPrintWriter pw) throws IOException {
        String techName = this.toString();
        pw.println("<?xml version='1.0' encoding='UTF-8'?>");
        header(pw);
        pw.println("<technology name='"+techName+"'");
        pw.adjustIndentation(4);
        pw.println("     xmlns='http://electric.sun.com/Technology'");
        pw.println("     xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'");
        pw.println("     xsi:schemaLocation='http://electric.sun.com/Technology ../../technology/Technology.xsd'>");
        pw.println("");
        pw.println("    <shortName>"+techName+"</shortName>");
        pw.println("    <description>"+getTechDescription()+"</description>");
        pw.println("    <numMetals min='"+(numLayersPolyOrMetal()-1)+"' max='"+(numLayersPolyOrMetal()-1)+"' default='"+(numLayersPolyOrMetal()-1)+"'/>");
        pw.println("    <scale value='1000.0' relevant='true'/>");
        pw.println("    <resolution value='"+gridAlignmentInUnits+"'/>");
        pw.println("    <defaultFoundry value='"+getFoundryName()+"'/>");
        pw.println("    <minResistance value='4.0'/>       <!-- not sure what this should be; copied default from mocmos -->");
        pw.println("    <minCapacitance value='0.1'/>      <!-- not sure what this should be; copied default from mocmos -->");
        pw.println("");
        pw.println("    <!-- Transparent layers; not sure why these are in a techfile, copied from mocmos -->");
        pw.println("    <transparentLayer transparent='1'><r>107</r><g>255</g><b>242</b></transparentLayer>");
        pw.println("    <transparentLayer transparent='2'><r>0</r><g>153</g><b>51</b></transparentLayer>");
        pw.println("    <transparentLayer transparent='3'><r>255</r><g>155</g><b>192</b></transparentLayer>");
        pw.println("    <transparentLayer transparent='4'><r>224</r><g>95</g><b>255</b></transparentLayer>");
        pw.println("    <transparentLayer transparent='5'><r>247</r><g>251</g><b>20</b></transparentLayer>");
        pw.println("");
        pw.println("    <!-- **************************************** LAYERS **************************************** -->");

        for(TechLayer tlayer : techLayers.values()) tlayer.dump(pw);
        for(TechLayer tlayer : techLayers.values()) tlayer.dumpArcs(pw);
        for(TechLayer tlayer : techLayers.values()) tlayer.dumpPins(pw);
        for(PrimitiveGroup nodeGroup : primitiveGroups) nodeGroup.dump(pw);

        pw.println("<spiceHeader level='1'><spiceLine line='* SPICE deck for "+getTechDescription()+"'/></spiceHeader>");
        pw.println("<spiceHeader level='2'><spiceLine line='* SPICE deck for "+getTechDescription()+"'/></spiceHeader>");
        pw.println("<spiceHeader level='3'><spiceLine line='* SPICE deck for "+getTechDescription()+"'/></spiceHeader>");
        dumpMenuPalette(pw);

        pw.println("<Foundry name='"+getFoundryName()+"'>");
        pw.adjustIndentation(4);
        for(TechLayer layer : techLayers.values())
            layer.dumpGdsMapping(pw);
        pw.adjustIndentation(4);
        for(TechLayer layer : techLayers.values())
            layer.dumpRules(pw);
        pw.adjustIndentation(-4);
        pw.println("</Foundry>");

        pw.adjustIndentation(-4);
        pw.println("</technology>");
        pw.flush();
    }

    /** override this in order to provide a default menu palette */
    protected void dumpMenuPalette(IndentingPrintWriter pw) throws IOException {
        pw.println("<menuPalette numColumns='3'>");
        pw.println("</menuPalette>");
    }

    // Helper Functions (abbreviations) //////////////////////////////////////////////////////////////////////////////
    
    public static LayerRules w(double width) { return wsa(width, 0, 0); }
    public static LayerRules ws(double width, double space) { return wsa(width, space, 0); }
    public static LayerRules wsa(double width, double space, double area) { return wsam(width, space, area, Double.MAX_VALUE); }
    public static LayerRules wsam(double width, double space, double area, double maxWidth) {
        return new LayerRules(width, space, area, maxWidth); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules, String description) {
        return new TechLayer(Tech.this, name, gdsGeom, gdsPin, drcRules, description); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules, GDSLayer res) {
        return new TechLayer(Tech.this, name, gdsGeom, gdsPin, drcRules, null, null, res, null, null); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules, String description, GDSLayer res) {
        return new TechLayer(Tech.this, name, gdsGeom, gdsPin, drcRules, null, description, res, null, null); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules, RenderingStyle rs, String description) {
        return new TechLayer(Tech.this, name, gdsGeom, gdsPin, drcRules, rs, description); }
    public TechLayer mkLayer(String name, LayerRules drcRules, RenderingStyle rs, String description) {
        return new TechLayer(Tech.this, name, null, null, drcRules, rs, description); }
    public TechLayer mkLayer(String name, RenderingStyle rs, String description) {
        return new TechLayer(Tech.this, name, null, null, description); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, String description, LayerRules drcRules) {
        return mkLayer(name, gdsGeom, gdsPin, drcRules, description); }
    public TechLayer mkLayer(String name, String description, LayerRules drcRules) { return mkLayer(name, null, null, drcRules, description); }
    public TechLayer mkLayer(String name, String description) { return mkLayer(name, (RenderingStyle)null, description); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules) { return mkLayer(name, gdsGeom, gdsPin, drcRules, ""); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin) { return new TechLayer(Tech.this, name, gdsGeom, gdsPin, null); }
    public TechLayer mkLayer(String name, LayerRules drcRules) { return mkLayer(name, null, null, drcRules, ""); }
    public TechLayer mkLayer(String name, LayerRules drcRules, String description) { return mkLayer(name, null, null, drcRules, description); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, String description) { return mkLayer(name,gdsGeom, gdsPin,null,description); }
    public TechLayer mkLayer(String name, GDSLayer gdsGeom, GDSLayer gdsPin, LayerRules drcRules, RenderingStyle explicitRS, String description,
                          GDSLayer gds_res, GDSLayer gds_dummy, GDSLayer gds_block, int metalNumber) {
        return new TechLayer(Tech.this, name, gdsGeom, gdsPin, drcRules, explicitRS, description, gds_res, gds_dummy, gds_block, metalNumber); }
    public GDSLayer g(int gdsMajor, int gdsMinor) { return new GDSLayer(gdsMajor, gdsMinor); }
    public GDSLayer g(String calibreName, int gdsMajor, int gdsMinor) { return new GDSLayer(calibreName, gdsMajor, gdsMinor); }


}
