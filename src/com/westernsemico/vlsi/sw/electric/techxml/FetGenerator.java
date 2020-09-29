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

/** currently out of service; do not use */
public class FetGenerator {
    /*
      protected Primitive mkFet(String name, boolean is_n, TechLayer... vtLayers) { return mkFet(name, is_n, 0.0, vtLayers); }
      protected Primitive mkFet(String name, boolean is_n, double Y_OFFSET, TechLayer... vtLayers) {
      double MIN_GRID = bgs.MIN_GRID;
      double MIN_GATE_WIDTH = bgs.MIN_GATE_WIDTH;
      double MIN_GATE_LENGTH = bgs.MIN_GATE_LENGTH;
      double MIN_GATE_SPACING = bgs.MIN_GATE_SPACING;
      double POLY_ENDCAP = bgs.POLY_ENDCAP;
      double SOURCE_DRAIN_LENGTH = bgs.SOURCE_DRAIN_LENGTH;
      double POLY_TO_DIFF = bgs.POLY_TO_DIFF;
      double WELL_SURROUND_TAP = bgs.WELL_SURROUND_TAP;
      double WELL_ENCLOSE_GATE_WIDTH_EDGE = bgs.WELL_ENCLOSE_GATE_WIDTH_EDGE;
      double WELL_ENCLOSE_GATE_LENGTH_EDGE = bgs.WELL_ENCLOSE_GATE_LENGTH_EDGE;
      double VT_EXTEND_GATE_WIDTH_EDGE = bgs.VT_EXTEND_GATE_WIDTH_EDGE;
      double VT_EXTEND_GATE_LENGTH_EDGE = bgs.VT_EXTEND_GATE_LENGTH_EDGE;
      double DCONT_TO_GATE = bgs.DCONT_TO_GATE;
      double DCONT_TO_POLY = bgs.DCONT_TO_POLY;
      double PCONT_TO_DIFF = bgs.PCONT_TO_DIFF;
      double SELECT_SURROUND_TAP = bgs.SELECT_SURROUND_TAP;
      double SELECT_SURROUND_ACT = bgs.SELECT_SURROUND_ACT;
      double SELECT_EXTEND_GATE_WIDTH_EDGE = bgs.SELECT_EXTEND_GATE_WIDTH_EDGE;
      double SELECT_EXTEND_GATE_LENGTH_EDGE = bgs.SELECT_EXTEND_GATE_LENGTH_EDGE;
      double MIN_POLY_SURROUND_PCONT = bgs.MIN_POLY_SURROUND_PCONT;

      Box           gateBox      = new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH).shift(0,Y_OFFSET);
      Primitive          fet          = new Primitive(Tech.this, name, is_n ? "TRANMOS" : "TRAPMOS", gateBox);
      Primitive.PrimitiveNet  gate         = fet.new PrimitiveNet("gate");

      double GATE_PORT_SHIFT     =
      (MIN_GATE_WIDTH / 2.0)
      + POLY_TO_DIFF
      + ((getLayer("Cont").drcRules.minWidth + MIN_POLY_SURROUND_PCONT) / 2.0);
      ;

      // the center of the diff port is moved from the center of the transistor:
      // - away from the center by half the gate length
      // - away from the center by the gate-to-contact distance
      // - away from the center by half the width of a contact
      double DIFF_PORT_SHIFT     =
      (MIN_GATE_LENGTH / 2.0) +
      DCONT_TO_GATE +
      (getLayer("Cont").drcRules.minWidth / 2.0);

      //
      // these four ports must appear in this specific order for NCC
      //
      Primitive.PrimitivePort gate_left    = fet.new PrimitivePort("gate-left",
      new ScaledBox(new Box(0.0, 0.0)).shift(-GATE_PORT_SHIFT,0)
      .shift(0,Y_OFFSET),
      gate,getLayer("Poly"));
      Primitive.PrimitivePort diff_top     = fet.new PrimitivePort("diff-top",
      new ScaledBox(new Box(MIN_GATE_WIDTH, 0.0))
      .shift(0, DIFF_PORT_SHIFT)
      .shift(0,Y_OFFSET),
      getLayer("Diff"));
      Primitive.PrimitivePort gate_right   = fet.new PrimitivePort("gate-right",
      new ScaledBox(new Box(0.0, 0.0))
      .shift( GATE_PORT_SHIFT,0)
      .shift(0,Y_OFFSET),
      gate,getLayer("Poly"));
      Primitive.PrimitivePort diff_bot     = fet.new PrimitivePort("diff-bot",
      new ScaledBox(new Box(MIN_GATE_WIDTH, 0.0))
      .shift(0,-DIFF_PORT_SHIFT)
      .shift(0,Y_OFFSET),
      getLayer("Diff"));
      Primitive.PrimitiveLayer left_endcap = fet.new Rectangle(getLayer("Poly"),
      gate_left,
      new ScaledBox(new Box(POLY_ENDCAP - POLY_TO_DIFF,
      MIN_GATE_LENGTH)
      .shift(-POLY_ENDCAP/2.0 -MIN_GATE_WIDTH/2.0
      - POLY_TO_DIFF/2.0, 0))
      .shift(0,Y_OFFSET)
      );
      Primitive.PrimitiveLayer right_endcap = fet.new Rectangle(getLayer("Poly"),
      gate_right,
      new ScaledBox(new Box(POLY_ENDCAP - POLY_TO_DIFF,
      MIN_GATE_LENGTH)
      .shift(POLY_ENDCAP/2.0 + MIN_GATE_WIDTH/2.0
      + POLY_TO_DIFF/2.0, 0))
      .shift(0,Y_OFFSET)
      );
      Primitive.PrimitiveLayer gate_layer   = fet.new Rectangle(getLayer("Poly"),
      new ScaledBox(MIN_GATE_WIDTH + 2.0 * POLY_TO_DIFF,
      MIN_GATE_LENGTH)
      .shift(0,Y_OFFSET));
      gate_layer.notElectrical = true;

      Primitive.PrimitiveLayer diff_nonelectrical_layer = fet.new Rectangle(getLayer("Diff"),
      new ScaledBox(MIN_GATE_WIDTH, MIN_GATE_LENGTH)
      .shift(0,Y_OFFSET));
      diff_nonelectrical_layer.notElectrical = true;

      Primitive.PrimitiveLayer diff_top_layer = fet.new Rectangle(getLayer("Diff"),
      diff_top,
      new ScaledBox(
      new Box(MIN_GATE_WIDTH,
      SOURCE_DRAIN_LENGTH)
      .shift(0, SOURCE_DRAIN_LENGTH/2.0+MIN_GATE_LENGTH/2.0))
      .shift(0,Y_OFFSET));

      Primitive.PrimitiveLayer diff_bot_layer = fet.new Rectangle(getLayer("Diff"),
      diff_bot,
      new ScaledBox(
      new Box(MIN_GATE_WIDTH,
      SOURCE_DRAIN_LENGTH)
      .shift(0,-SOURCE_DRAIN_LENGTH/2.0-MIN_GATE_LENGTH/2.0))
      .shift(0,Y_OFFSET));
      fet.new Rectangle(getLayer(is_n ? "Well-P" : "Well-N"),
      new ScaledBox(new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH)
      .grow(WELL_ENCLOSE_GATE_LENGTH_EDGE, WELL_ENCLOSE_GATE_WIDTH_EDGE))
      .shift(0,Y_OFFSET));
      fet.new Rectangle(getLayer(is_n ? "Select-NAct" : "Select-PAct"),
      new ScaledBox(new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH)
      .grow(SELECT_EXTEND_GATE_LENGTH_EDGE, SELECT_EXTEND_GATE_WIDTH_EDGE))
      .shift(0,Y_OFFSET));
      for(TechLayer vtLayer : vtLayers)
      fet.new Rectangle(vtLayer,
      new ScaledBox(new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH)
      .grow(VT_EXTEND_GATE_LENGTH_EDGE, VT_EXTEND_GATE_WIDTH_EDGE))
      .shift(0,Y_OFFSET));
      return fet;
      }

    
      protected final BasicGeometricSpecs bgs = new BasicGeometricSpecs();
      public class BasicGeometricSpecs {
      private BasicGeometricSpecs() { }
      double MIN_GRID;
      double MIN_GATE_WIDTH;
      double MIN_GATE_LENGTH;
      double MIN_GATE_SPACING;
      double POLY_ENDCAP;
      double SOURCE_DRAIN_LENGTH;
      double POLY_TO_DIFF;
      double WELL_SURROUND_TAP;
      double WELL_SURROUND_ACT;
      double WELL_ENCLOSE_GATE_WIDTH_EDGE;
      double WELL_ENCLOSE_GATE_LENGTH_EDGE;
      double VT_EXTEND_GATE_WIDTH_EDGE;
      double VT_EXTEND_GATE_LENGTH_EDGE;
      double DCONT_TO_GATE;
      double DCONT_TO_POLY;
      double PCONT_TO_DIFF;
      double SELECT_SURROUND_TAP;
      double SELECT_SURROUND_ACT;
      double SELECT_EXTEND_GATE_WIDTH_EDGE;
      double SELECT_EXTEND_GATE_LENGTH_EDGE;
      double MIN_POLY_SURROUND_PCONT;
      double MIN_TAP_AREA;
      }

    protected void mkTaps() {
        double minWidth = getLayer("Diff").drcRules.minWidth;
        for(boolean ptap : new boolean[] { true, false }) {
            Box diff     = new Box(minWidth, bgs.MIN_TAP_AREA / minWidth);
            Box select   = diff.grow(bgs.SELECT_SURROUND_TAP, bgs.SELECT_SURROUND_TAP);
            Box nodeBase = diff;  // use the select as the nodebase since it is what hits the DRC rules
            Primitive tapPrimitive = new Primitive(Tech.this, "Welltap-"+(ptap?"P":"N"), new PrimitiveGroup(Tech.this), "CONTACT", nodeBase);
            Primitive.PrimitivePort port = tapPrimitive.new PrimitivePort("port", new ScaledBox(diff), getLayer("Diff"));
            tapPrimitive.new Rectangle(getLayer("Diff"), port, new ScaledBox(diff));
            tapPrimitive.new Rectangle(getLayer("Well-"+(ptap?"P":"N")), new ScaledBox(diff.grow(bgs.WELL_SURROUND_TAP)));
            tapPrimitive.new Rectangle(getLayer("Select-"+(ptap?"P":"N")+"Tap"), new ScaledBox(diff.grow(bgs.SELECT_SURROUND_TAP)));
        }
    }
    

    */
}
