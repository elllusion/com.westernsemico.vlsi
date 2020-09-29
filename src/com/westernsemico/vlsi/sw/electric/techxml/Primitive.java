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

/** Encapsulates a <primitiveNode> element in an Electric Technology XML file */
public class Primitive {

    /** the technology that this node is part of */
    public final Tech   tech;

    /** the name of this primitive node */
    public final String name;

    /** the "function" of this primitive node, as described by the "fun" attribute on the "primitiveNode" element */
    public final String fun;

    /**
     *  PrimitiveBase is the size of the "prototypical" version of this
     *  node; all the other geometries are specified relative to
     *  it, and the red "highlight" when you mouse over a node
     *  shows the size of the nodeBase rectangle after applying
     *  that nodeinst's sizing.
     */
    public final Box    nodeBase;

    /** default width of the primitive */
    public final double defaultWidth;

    /** default height of the primitive */
    public final double defaultHeight;

    /** minimum width of the primitive */
    private      double minWidth = 0.0;

    /** minimum height of the primitive */
    private      double minHeight = 0.0;

    /** the number of Nets created; this is incremented each time one is added and used as the <portTopology> identifier */
    private int nodeNetNums = 0;

    /** the number of Ports created; this is incremented each time one is added and used as the <portNum> identifier */
    private int numPorts = 0;

    /** Ports; their order matters! See Tech XML documentation. */
    private ArrayList<Port> nodePorts = new ArrayList<Port>();

    /** The layers in this primitive node */
    private ArrayList<Layer> nodeLayers = new ArrayList<Layer>();


    public Primitive(Tech tech, String name, PrimitiveGroup group, String fun, Box nodeBase, double defaultWidth, double defaultHeight) {
        this.tech = tech;
        this.name = name;
        this.fun = fun;
        this.nodeBase = nodeBase;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        group.primitives.add(this);
    }
    public Primitive(Tech tech, String name, PrimitiveGroup group, String fun, Box nodeBase) {
        this(tech, name, group, fun, nodeBase, nodeBase.getWidth(), nodeBase.getHeight()); }

    public void setMinDimensions(Box box) { setMinDimensions(box.getWidth(), box.getHeight()); }
    public void setMinDimensions(double minWidth, double minHeight) { this.minWidth = minWidth; this.minHeight = minHeight; }

    /** dump the Technology XML File fragment */
    public void dump(IndentingPrintWriter pw) throws IOException {
        pw.println("<primitiveNode name='"+name+"' fun='"+fun+"'/>");
        pw.adjustIndentation(4);
        if (defaultWidth != nodeBase.getWidth())
            pw.println("<defaultWidth><lambda>"+
                       ((defaultWidth - nodeBase.getWidth()))+"</lambda></defaultWidth>");
        if (defaultHeight != nodeBase.getHeight())
            pw.println("<defaultHeight><lambda>"+
                       ((defaultHeight - nodeBase.getHeight()))+"</lambda></defaultHeight>");
            
        pw.println("<nodeBase>");
        pw.adjustIndentation(4);
        new ScaledBox(nodeBase).dump(pw);
        pw.adjustIndentation(-4);
        pw.println("</nodeBase>");

        for(Layer nodeLayer : nodeLayers)  nodeLayer.dump(pw);
        for(Port nodePort : nodePorts)     nodePort.dump(pw);

        if (minWidth>0 || minHeight>0)
            pw.println("<minSizeRule width='"+(minWidth)+"' height='"+(minHeight)+"' rule='"+name+".MINSIZE'/>");

        pw.adjustIndentation(-4);
    }

    /** A port on a primitive node, which can be wired or exported */
    public class Port {
        public final String name;
        public final ScaledBox extent;
        public final Set<TechLayer> layers;
        public final Net net;
        private final int portNum;   // this must be the ordinal index of the Port, as written in the XML file
        public Port(String name, Box extent, TechLayer... layers) { this(name, new ScaledBox(extent), null, layers); }
        public Port(String name, ScaledBox extent, TechLayer... layers) { this(name, extent, null, layers); }
        public Port(String name, ScaledBox extent, Net net, TechLayer... layers) { 
            this.name = name;
            this.net = net==null ? new Net(name) : net;
            this.extent = extent;
            HashSet<TechLayer> arr = new HashSet<TechLayer>();
            for(TechLayer tlayer : layers) if (tlayer != null) arr.add(tlayer);
            this.layers = Collections.unmodifiableSet(arr);
            this.portNum = numPorts++;
            nodePorts.add(this);
        }
        public Primitive getPrimitive() { return Primitive.this; }
        public void dump(IndentingPrintWriter pw) throws IOException {
            pw.println("<primitivePort name='"+name+"'>");
            pw.adjustIndentation(4);
            pw.println("<portAngle primary='0' range='180'/>");
            pw.println("<portTopology>"+(net.nodeNetNum+1)+"</portTopology> <!-- "+net.comment+" -->");
            extent.dump(pw);
            for(TechLayer tlayer : layers)
                pw.println("<portArc>"+tlayer+"</portArc>");
            pw.adjustIndentation(-4);
            pw.println("</primitivePort>");
        }
    }

    /**
     *  An electrical network exposed as one or more ports.  Only
     *  necessary when two or more ports are on the same net --
     *  i.e. those ports are electrically connected by the primitive
     *  itself, like the top and bottom endcaps of a mosfet gate.
     */
    public class Net {
        public final int nodeNetNum;
        public final String comment;
        public Net(String comment) {
            this.comment = comment;
            this.nodeNetNum = nodeNetNums++;
        }
    }

    /**
     *  One layer of a primitive node.  Primitive nodes often involve
     *  multiple layers; for example a mosfet primitive will have
     *  Layers for its diffusion, for its poly, and its well (and
     *  perhaps others too).
     */
    public abstract class Layer {
        public final TechLayer   tlayer;
        public final Port port;
        public boolean notElectrical = false;
        public Layer(TechLayer tlayer, Port port) {
            this.tlayer = tlayer;
            this.port = port;
            nodeLayers.add(this);
        }
        public abstract void dump(IndentingPrintWriter pw) throws IOException;
    }

    // Subclasses of Primitive.Layer //////////////////////////////////////////////////////////////////////////////

    /** a Layer whose shape is a rectangle */
    public class Rectangle extends Layer {
        public final ScaledBox lambdaBox;
        public Rectangle(TechLayer tlayer, Port port, Box lambdaBox) { this(tlayer, port, new ScaledBox(lambdaBox)); }
        public Rectangle(TechLayer tlayer, Port port, ScaledBox lambdaBox) { 
            super(tlayer, port);
            this.lambdaBox = lambdaBox;
        }
        public Rectangle(TechLayer tlayer, Box box) { this(tlayer, new ScaledBox(box)); }
        public Rectangle(TechLayer tlayer, ScaledBox lambdaBox) { this(tlayer, null, lambdaBox); }
        public Rectangle(TechLayer tlayer, double width, double height) { this(tlayer, null, width, height); }
        public Rectangle(TechLayer tlayer, Port port, double width, double height) {
            this(tlayer, port, new ScaledBox(width, height)); }
        public void dump(IndentingPrintWriter pw) throws IOException {
            pw.println("<nodeLayer layer='"+tlayer+"' style='FILLED'"+
                       (port==null?" portNum='-1'":" portNum='"+port.portNum+"'")+
                       (notElectrical?" electrical='false'":"")+
                       ">");
            pw.adjustIndentation(4);
            lambdaBox.dump(pw);
            pw.adjustIndentation(-4);
            pw.println("</nodeLayer>");
        }
    }

    /** a Layer whose shape is a circle (not generally used in VLSI, but useful for printed circuit board vias) */    
    public class Round extends Layer {
        public final double diameter;
        public final double thickness;
        public final double mult;
        public Round(TechLayer tlayer, Port port, double diameter) { this(tlayer, port, diameter, 0.0); }
        public Round(TechLayer tlayer, Port port, double diameter, double thickness) { this(tlayer, port, diameter, thickness, 1.0); }
        public Round(TechLayer tlayer, Port port, double diameter, double thickness, double mult) {
            super(tlayer, port);
            this.diameter = diameter;
            this.thickness = thickness;
            this.mult = 0.5 * mult;
        }
        public void dump(IndentingPrintWriter pw) throws IOException {
            pw.println("<nodeLayer layer='"+tlayer+"' style='"+(thickness==0?"DISC":"THICKCIRCLE")+"'"+
                       (port==null?" portNum='-1'":" portNum='"+port.portNum+"'")+
                       (notElectrical?" electrical='false'":"")+
                       ">");
            pw.adjustIndentation(4);
            pw.println("<points/>");
            pw.println("<techPoint xm='0.0' xa='0.0' ym='0.0' ya='0.0'/>");
            pw.println("<techPoint xm='"+mult+"' xa='"+(diameter/2)+"' ym='0.0' ya='0.0'/>");
            pw.adjustIndentation(-4);
            pw.println("</nodeLayer>");
        }
    }

    /** a Layer which is filled with an array of square "cuts"; used in VLSI for contacts and vias */
    public class MultiCut extends Layer {
        public final double cutWidth;
        public final double cutHeight;
        public final double cutSpacing1d;
        public final double cutSpacing2d;
        /** the center-points of the cuts will always fall within this box */
        public final Box    boundingBoxOfCentroids;

        public MultiCut(TechLayer tlayer,
                        double cutWidth, double cutHeight, double cutSpacing1d, double cutSpacing2d,
                        Box    boundingBoxOfCuts) {
            super(tlayer, null);
            this.cutWidth = cutWidth;
            this.cutHeight = cutHeight;
            this.cutSpacing1d = cutSpacing1d;
            this.cutSpacing2d = cutSpacing2d;
            this.boundingBoxOfCentroids = boundingBoxOfCuts.grow(-cutWidth/2.0, -cutHeight/2.0);
        }
        public void dump(IndentingPrintWriter pw) throws IOException {
            pw.println("<nodeLayer layer='"+tlayer+"' style='FILLED'>");
            pw.adjustIndentation(4);
            pw.println("<multicutbox"+
                       " sizex='"+(cutWidth)+"'"+
                       " sizey='"+(cutWidth)+"'"+
                       " sep1d='"+(cutSpacing1d)+"'"+
                       " sep2d='"+(cutSpacing2d)+"'"+
                       ">");
            pw.adjustIndentation(4);
            pw.println("<lambdaBox"+boundingBoxOfCentroids+"/>");
            pw.adjustIndentation(-4);
            pw.println("</multicutbox>");
            pw.adjustIndentation(-4);
            pw.println("</nodeLayer>");
        }
    }   
}
