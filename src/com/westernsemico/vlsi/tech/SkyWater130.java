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
package com.westernsemico.vlsi.tech;
import com.westernsemico.vlsi.sw.electric.techxml.*;
import com.westernsemico.util.*;
import java.util.*;
import java.io.*;

/** SkyWater 130nm */
public class SkyWater130 extends Tech {

    // Technology Implementation //////////////////////////////////////////////////////////////////////////////

    // hand-transcribed values
    public static final double GRID_SIZE_DRAWN_UM = 0.005;      // assumptions.html, table 1 "General", row "Grid Size - Drawn"

    public SkyWater130() { super(GRID_SIZE_DRAWN_UM); }
    
    public String toString()                 { return "skywater130"; }
    public String getTechDescription()       { return "SkyWater 130nm"; }
    public double getFoundryRecommendedVdd() { return 1.8; }
    public String getFoundryName()           { return "SkyWater"; }

    @Override public int    lowestMetalLayerOrdinal()                     { return 0; }
    @Override public int    numLayersPolyOrMetal()                        { return 7; } // Poly+LI+M{12345}
    @Override public double getNfetGateCapacitanceFemtoFaradsForX1()      { throw new RuntimeException("FIXME"); }
    @Override public double getNfetDrainCapacitanceFemtoFaradsForX1()     { throw new RuntimeException("FIXME"); }
    @Override public double getCapacitanceFemtoFaradsPerNm(int layer)     { throw new Error("FIXME"); }
    @Override public double getResistanceOhmsPerSquare(int layer)         { throw new Error("FIXME"); }
    
    @Override public void dump(IndentingPrintWriter pw) throws IOException {

        // TODO/FIXME: gate encap spacing must be 0.21, gate must be 0.15
        // TODO/FIXME: PWELL has no drawn layer but has pins on gds 64:59?
        // TODO/FIXME: pwelliso,label,44:5,(Text type)
        // TODO/FIXME: inductor,label,82:25,
        
        // Copied from gds_layers.csv, all rows marked "drawing" or "drawing, text"; pins are from rows marked "label"
        TechLayer dnwell    = mkLayer("dnwell",    g("dnwell",     64,18), null   , ws (3.00, 6.30)         , "Deep n-well region");
        TechLayer nwell     = mkLayer("nwell",     g("nwell",      64,20),g(64, 5), ws (0.84, 1.27)         , "N-well region");
        TechLayer pwbm      = mkLayer("pwbm",      g("pwbm",       19,44), null   , ws (0.84, 1.27)         , "Regions (in UHVI) blocked from p-well implant (DE MOS devices only)");
        TechLayer pwde      = mkLayer("pwde",      g("pwde",      124,20), null   , ws (0.84, 1.27)         , "Regions to receive p-well drain-extended implants");
        TechLayer diff      = mkLayer("diff",      g("diff",       65,20),g(65, 5), ws (0.14, 0.27)         , "Active (diffusion) area (type opposite of well/substrate underneath)");
        TechLayer tap       = mkLayer("tap",       g("tap",        65,44),g(65, 5), ws (0.14, 0.27)         , "Active (diffusion) area (type equal to the well/substrate underneath)");
        TechLayer hvtr      = mkLayer("hvtr",      g("hvtr",       18,20), null   , ws (0.38, 0.38)         , "High-Vt RF transistor implant");
        TechLayer hvtp      = mkLayer("hvtp",      g("hvtp",       78,44), null   , wsa(0.38, 0.38, 0.265)  , "High-Vt LVPMOS implant");
        TechLayer lvtn      = mkLayer("lvtn",      g("lvtn",      125,44), null   , wsa(0.38, 0.38, 0.265)  , "Low-Vt NMOS device");
        TechLayer ncm       = mkLayer("ncm",       g("ncm",        92,44), null   , wsa(0.38, 0.38, 0.265)  , "N-core implant");
        TechLayer tunm      = mkLayer("tunm",      g("tunm",       80,20), null   , wsa(0.41, 0.50, 0.672)  , "SONOS device tunnel implant");
        TechLayer rpm       = mkLayer("rpm",       g("rpm",        86,20), null   , ws (1.27, 0.84)         , "300 ohms/square polysilicon resistor implant");
        TechLayer ldntm     = mkLayer("ldntm",     g("ldntm",      11,44), null   , ws (0.70, 0.70)         , "N-tip implant on SONOS devices");
        TechLayer hvntm     = mkLayer("hvntm",     g("hvntm",     125,20), null   , ws (0.70, 0.70)         , "High voltage N-tip implant");
        TechLayer npc       = mkLayer("npc",       g("npc",        95,20), null   , ws (0.27, 0.27)         , "Nitride poly cut (under licon1 areas)");
        TechLayer hvi       = mkLayer("hvi",       g("hvi",        75,20), null   , ws (0.70, 0.70)         , "High voltage (5.0V) thick oxide gate regions");
        TechLayer nsdm      = mkLayer("nsdm",      g("nsdm",       93,44), null   , wsa(0.38, 0.38, 0.265)  , "N+ source/drain implant");
        TechLayer psdm      = mkLayer("psdm",      g("psdm",       94,20), null   , wsa(0.38, 0.38, 0.255)  , "P+ source/drain implant");
        TechLayer poly      = mkLayer("poly",      g("poly",       66,20),g(66, 5), ws (0.15,0.175)         , "Polysilicon (tapes out to same GDS layer as Gate)");
        TechLayer gate_     = mkLayer("gate",      g("gate",       66,20),g(66, 5), ws (0.14,0.160)         , "Gate (tapes out to same GDS layer as Poly)");
        TechLayer urpm      = mkLayer("urpm",      g("urpm",       79,20), null   , ws (1.27, 0.84)         , "2000 ohms/square polysilicon resistor implant");
        TechLayer licon1    = mkLayer("licon1",    g("licon1",     66,44), null   , ws (0.17, 0.17)         , "Contact to local interconnect");
        TechLayer li1       = mkLayer("li1",       g("li1",        67,20),g(67, 5), wsa(0.14, 0.14, 0.0561) , null, "Local interconnect", null, null, null, 0);
        TechLayer mcon      = mkLayer("mcon",      g("mcon",       67,44), null   , ws (0.17, 0.19)         , "Contact from local interconnect to metal1");  // can be rectangular?
        TechLayer met1      = mkLayer("met1",      g("met1",       68,20),g(68, 5), wsa(0.14, 0.14, 0.083)  , null, "Metal 1", null, null, null, 1);
        TechLayer via_      = mkLayer("via",       g("via",        68,44), null   , ws (0.15, 0.17)         , "Contact from metal 1 to metal 2");
        TechLayer met2      = mkLayer("met2",      g("met2",       69,20),g(69, 5), wsa(0.14, 0.14, 0.0676) , null, "Metal 2", null, null, null, 2);
        TechLayer via2      = mkLayer("via2",      g("via2",       69,44), null   , ws (0.20, 0.20)         , "Contact from metal 2 to metal 3");
        TechLayer met3      = mkLayer("met3",      g("met3",       70,20),g(70, 5), wsa(0.30, 0.30, 0.24)   , null, "Metal 3", null, null, null, 3);
        TechLayer via3      = mkLayer("via3",      g("via3",       70,44), null   , ws (0.20, 0.20)         , "Contact from metal 3 to metal 4");
        TechLayer met4      = mkLayer("met4",      g("met4",       71,20),g(71, 5), wsa(0.30, 0.30, 4)      , null, "Metal 4", null, null, null, 4);
        TechLayer via4      = mkLayer("via4",      g("via4",       71,44), null   , ws (0.80, 0.80)         , "Contact from metal 4 to metal 5");
        TechLayer met5      = mkLayer("met5",      g("met5",       72,20),g(72, 5), ws (0.80, 0.80)         , null, "Metal 5", null, null, null, 5);
        TechLayer nsm       = mkLayer("nsm",       g("nsm",        61,20), null   , ws (3.00, 4.00)         , "Nitride seal mask");
        TechLayer pad       = mkLayer("pad",       g("pad",        76,20),g(76, 5), ws (2.00, 1.27)         , "Passivation cut (opening over pads)");
        TechLayer capm      = mkLayer("capm",      g("capm",       89,44), null   , ws (2.00, 0.84)         , "MiM capacitor plate over metal 3");
        TechLayer cap2m     = mkLayer("cap2m",     g("cap2m",      97,44), null   , ws (2.00, 0.84)         , "MiM capacitor plate over metal 4");
        TechLayer vhvi      = mkLayer("vhvi",      g("vhvi",       74,21), null   , w  (0.02      )         , "12V nominal (16V max) node identifier");
        TechLayer uhvi      = mkLayer("uhvi",      g("uhvi",       74,22), null   , w  (0.02      )         , "20V nominal node identifier");
        TechLayer npn       = mkLayer("npn",       g("npn",        82,20), null                             , "Base region identifier for NPN devices");
        TechLayer inductor  = mkLayer("inductor",  g("inductor",   82,24), null                             , "Identifier for inductor regions");
        TechLayer capacitor = mkLayer("capacitor", g("capacitor",  82,64), null                             , "Identifier for interdigitated (vertical parallel plate (vpp)) capacitors");
        TechLayer pnp       = mkLayer("pnp",       g("pnp",        82,44), null                             , "Base nwell region identifier for PNP devices");
        TechLayer LVS       = mkLayer("LVS prune", g("LVS prune",  84,44), null                             , "Exemption from LVS check (used in e-test modules only)");
        TechLayer padCenter = mkLayer("padCenter", g("padCenter",  81,20), null                             , "Pad center marker");
        TechLayer target    = mkLayer("target",    g("target",     76,44), null                             , "Metal fuse target");

        // these layers appear as both "drawing" and "mask" (with different GDS numbers).
        // I guess they are for non-OPC'd, non-post-processed geometry?
        /*
        TechLayer conom     = mkLayer("conom",     g("conom",      87,44 ), "");
        TechLayer clvom     = mkLayer("clvom",     g("clvom",      45,20 ), "");
        TechLayer cfom     = mkLayer("cfom",      g("cfom",       22,20 ), "Field oxide mask");
        TechLayer clvtnm     = mkLayer("clvtnm",    g("clvtnm",     25,44 ), "");
        TechLayer chvtpm     = mkLayer("chvtpm",    g("chvtpm",     88,44 ), "");
        TechLayer cntm     = mkLayer("cntm",      g("cntm",       26,20 ), "");
        TechLayer chvntm     = mkLayer("chvntm",    g("chvntm",     38,20 ), "");
        TechLayer cnpc     = mkLayer("cnpc",      g("cnpc",       44,20 ), "");
        TechLayer cnsdm     = mkLayer("cnsdm",     g("cnsdm",      29,20 ), "");
        TechLayer cpsdm     = mkLayer("cpsdm",     g("cpsdm",      31,20 ), "");
        TechLayer cli1m     = mkLayer("cli1m",     g("cli1m",     115,44 ), "");
        TechLayer cviam3     = mkLayer("cviam3",    g("cviam3",    112,20 ), "");
        TechLayer cviam4     = mkLayer("cviam4",    g("cviam4",    117,20 ), "");
        TechLayer cncm     = mkLayer("cncm",      g("cncm",       96,44 ), "");
        */

        // Layers for Electric's internal use -- these distinguish GDS
        // layers that have different purposes so Electric can apply
        // different DRC rules to each case.  These layers are omitted
        // from the GDS streamout.
        mkLayer("Electric-PCont",     "Electric non-GDS layer to designate poly contacts");
        mkLayer("Electric-DCont",     "Electric non-GDS layer to designate diffusion contacts");
        mkLayer("Electric-NAct",      "Electric non-GDS layer to designate N+ Active Diffusion");
        mkLayer("Electric-PAct",      "Electric non-GDS layer to designate P+ Active Diffusion");
        mkLayer("Electric-NTap",      "Electric non-GDS layer to designate N-Tap");
        mkLayer("Electric-PTap",      "Electric non-GDS layer to designate P-Tap");

        
        // Layer Heights/Thicknesses (3D) /////////////////////////////////////////////////////////////////////////////////////////

        double height3D = 0;
        dnwell.height3D = 0;
        dnwell.thick3D = 3.0;   // made-up value; couldn't find this in the PDK
        nwell.thick3D  = 1.1;   // from "Table 3b - Semiconductor Criteria - Junction Depths
        double substrateTop3D = dnwell.height3D+dnwell.thick3D;
        nwell.height3D  = substrateTop3D-nwell.thick3D;
        nsdm.thick3D    = 0.1;   // from "Table 3b - Semiconductor Criteria - Junction Depths"
        nsdm.height3D   = substrateTop3D-nsdm.thick3D;
        psdm.thick3D    = 0.1;   // from "Table 3b - Semiconductor Criteria - Junction Depths"
        psdm.height3D   = substrateTop3D-psdm.thick3D;
        diff.thick3D    = 0.12;  // from "Process Stack Diagram"
        diff.height3D   = substrateTop3D-diff.thick3D;
        tap.thick3D     = 0.12;  // tap is manufactured as diff
        tap.height3D    = substrateTop3D-tap.thick3D;

        poly.thick3D    = 0.18; poly.height3D = substrateTop3D + 0.3262;   // TODO: render gate poly at a lower height
        hvntm.thick3D   = poly.thick3D; hvntm.height3D = poly.height3D; 
        rpm.thick3D     = poly.thick3D; rpm.height3D = poly.height3D; 
        urpm.thick3D    = poly.thick3D; urpm.height3D = poly.height3D; 

        npc.height3D    = substrateTop3D; npc.thick3D = 0.0;  // FIXME: how to render this?
        
        licon1.height3D = poly.height3D+poly.thick3D ; licon1.thick3D = 0.4299;  // FIXME: render diffusion contacts all the way down to the substrate
        li1.height3D    = licon1.height3D+licon1.thick3D; li1.thick3D = 0.1;
        mcon.height3D   = li1.height3D+li1.thick3D   ; mcon.thick3D   = 0.265+0.075;  // from "Process Stack Diagram"
        met1.height3D   = mcon.height3D+mcon.thick3D ; met1.thick3D   = 0.36;
        via_.height3D   = met1.height3D+met1.thick3D ; via_.thick3D    = 0.27;
        met2.height3D   = via_.height3D+via_.thick3D  ; met2.thick3D   = 0.36;
        via2.height3D   = met2.height3D+met2.thick3D ; via2.thick3D   = 0.42;
        met3.height3D   = via2.height3D+via2.thick3D ; met3.thick3D   = 0.845;
        via3.height3D   = met3.height3D+met3.thick3D ; via3.thick3D   = 0.39;
        met4.height3D   = via3.height3D+via3.thick3D ; met4.thick3D   = 0.845;
        via4.height3D   = met4.height3D+met4.thick3D ; via4.thick3D   = 0.505;
        met5.height3D   = via4.height3D+via4.thick3D ; met5.thick3D   = 1.26;
        nsm.height3D    = met5.height3D+met5.thick3D ; nsm.thick3D    = 0.009+0.54;
        pad.height3D    = pad.height3D+pad.thick3D   ; pad.thick3D    = 5.2523-nsm.thick3D;

        // these thicknesses are not specified, made up numbers
        // FIXME: via4 above cap2m is not as tall as ordinary via4; same for via3/capm
        cap2m.thick3D  = via4.height3D/2.0;  cap2m.height3D = met4.height3D-cap2m.thick3D;
        capm.thick3D   = via3.height3D/2.0;  capm.height3D  = met3.height3D-capm.thick3D;

        // not rendered
        pwbm.thick3D = 0.0; pwbm.height3D = substrateTop3D;
        pwde.thick3D = 0.0; pwde.height3D = substrateTop3D;
        hvtr.thick3D = 0.0; hvtr.height3D = substrateTop3D;
        hvtp.thick3D = 0.0; hvtp.height3D = substrateTop3D;
        ldntm.thick3D = 0.0; ldntm.height3D = substrateTop3D;
        hvi.thick3D = 0.0; hvi.height3D = substrateTop3D;
        tunm.thick3D = 0.0; tunm.height3D = substrateTop3D;
        lvtn.thick3D = 0.0; lvtn.height3D = substrateTop3D;

        // Interlayer spacing rules //////////////////////////////////////////////////////////////////////////////
        
        double MIN_GATE_WIDTH                   = 0.140;
        double MIN_GATE_LENGTH                  = 0.150;
        double POLY_TO_DIFF                     = 0.065;  // "Spacing of poly on field to diff"
        double POLY_TO_TAP                      = 0.005;  // "Spacing of poly on field to tap"
        double MIN_GRID                         = 0.005;
        double POLY_ENDCAP                      = 0.150;  // FIXME: space from endcap to any other poly must be 0.21
        double POLY_ENCLOSURE_OF_PCONT          = 0.050;  // FIXME: unsure
        double DCONT_TO_GATE                    = 0.055;  // FIXME: inferred from layout examples, cannot find rule
        double SOURCE_DRAIN_LENGTH              = 0.260;  // FIXME: inferred from layout examples, cannot find rule
        double SELECT_SURROUND_ACT              = 0.125;  // nsd.5a/psd.5a
        double VT_SURROUND_GATE                 = 0.180;  // lvtn.4b
        double DIFF_ENCLOSURE_OF_DCONT          = 0.040;  // FIXME: inferred from layout examples, cannot find rule
        double PCONT_TO_DIFF                    = 0.190;  // licon.14: 0.19 min. spacing of poly_licon1 & "diffTap" in periphery

        getLayer("Electric-PCont").addUnconnectedSpacingRule(getLayer("diff"), PCONT_TO_DIFF);
        getLayer("nsdm").forbidden(getLayer("psdm"));
        getLayer("poly").addSpacingRule(getLayer("diff"), POLY_TO_DIFF);        // poly.c1: 0.03 min. spacing of "poly" in core & diff
        getLayer("poly").addSpacingRule(getLayer("tap"), POLY_TO_DIFF);         // poly.c1: 0.03 min. spacing of "poly" in core & tap
        getLayer("lvtn").addSpacingRule(getLayer("hvtp"),  0.38);               // lvtn.9:      0.38 min. spacing of lvtn & hvtp
        getLayer("lvtn").addSpacingRule(getLayer("nwell"), 0.38);               // lvtn.12:     0.38 min. spacing of lvtn & coreNwell
        getLayer("hvtr").addSpacingRule(getLayer("hvtp"),  0.38);               // hvtr.2:      0.38 min. spacing of hvtr & hvtp
        getLayer("Electric-NAct").addSpacingRule(getLayer("nwell"),  0.32);     // difftap.c13: 0.32 min. spacing of "ndiff" in core & nwell
        getLayer("nsdm").addSpacingRule(getLayer("Electric-PTap"),  0.13);      // nsd.7:       0.13 min. spacing of nsdm & opposite implant diffTap
        getLayer("psdm").addSpacingRule(getLayer("Electric-NTap"),  0.13);      // psd.7:       0.13 min. spacing of psdm & opposite implant diffTap
        getLayer("npc").addSpacingRule(getLayer("gate"),  0.09);                // npc.4:       0.09 min. spacing of npc & gate
        getLayer("rpm").addSpacingRule(getLayer("nsdm"),  0.20);                // rpm.6:       0.20 min. spacing of rpm & nsdm
        getLayer("rpm").addSpacingRule(getLayer("poly"),  0.20);                // rpm.7:       0.20 min. spacing of rpm & poly

        // spacing rules still to be implemented
        //
        // r443,difftap.11,0.13 min. spacing of PTAP_nonUHVI & nwell
        // r495,nwell.8,2 min. spacing of HV_nwell & nwell
        // r496,hv.nwell.1,2.5 min. spacing of nwell with text (shv_nwell) & nwell
        // r502,difftap.15b,0.37 min. spacing of n+ diff inside hvi in periphery & ptapHV_PERI_noAbut
        // r506,difftap.18,0.43 min. spacing of ndiff_nonESDuhvi & HV_nwell
        // r508,difftap.20,0.43 min. spacing of PTAP_noPwellRes_noUHVI & HV_nwell
        // r511,difftap.23,0.18 min. spacing of diffTapNoHv_PERI & hvi
        // r512,difftap.24,0.43 min. spacing of ndiffHV_nonESDuhvi & nwell
        // r528,poly.9,0.48 min. spacing of poly resistor & diffTap
        // r530,poly.9,0.48 min. spacing of poly resistor & poly
        // r718,licon.5b,0.06 min. spacing of tapLicon_PERI & diffTap butting edge
        // r724,licon.10,0.25 min. spacing of varLiconPer & varChannel
        // r746,licon.9,0.11 min. spacing of polyLicon1OutRpm & psdm
        // r748,licon.13,"0.09 min. spacing of ""licon1 on diffTap"" in periphery & npc"
        // r750,licon.14,"0.19 min. spacing of poly_licon1 & ""diffTap"" in periphery"
        // r760,licon.c1,"0.13 min. spacing of poly_licon1 & ""diffTap"" in core"
        // r910,denmos.11,0.86 min. spacing of ptap & nwellOVRdeNFetDrain
        // r1030,ncm.c9,0.235 min. spacing of ncm_CORE not tech_CD & ndiff
        // r1032,ncm.c10,0.38 min. spacing of nwellOutCore & ncm_CORE not tech_CD
        // r1038,ldntm.c6,0.18 min. spacing of ldntmCoreExempt & pdiff
        // r1057,rpm.9,0.185 min. spacing of precResistor & hvntm
        // r1059,rpm.10,2 min. spacing of rpmNotXmt & pwbm
        // r1154,vpp.9,1.5 min. spacing of capacitor & nwell
        // r1163,vhvi.8,11.24 min. spacing of VHVnwell & VHVnwellNoConn

        // nsd.8: nsdm must not overlap pdiff/ptap (source of extendedDrain fet exempted)
        getLayer("nsdm").forbidden(getLayer("psdm"));

        // Transistor Primitives //////////////////////////////////////////////////////////////////////////////

        //
        // The most common case is placing a PCONT at the poly port,
        // maybe nudged sideways.  Therefore we place the port so that
        // a poly contact lands at the minimum spacing from the DIFF:
        //
        // the center of the gate port is moved from the center of the transistor:
        // - away from the center by half the gate width
        // - away from the center by the poly-diff spacing
        // - away from the center by half the PCONT width on the narrow axis
        double GATE_PORT_OFFSET_FROM_CENTER =
            (MIN_GATE_WIDTH / 2.0)
            + POLY_TO_DIFF
            + POLY_ENCLOSURE_OF_PCONT
            + (getLayer("licon1").layerRules.minWidth / 2.0)
            ;
        GATE_PORT_OFFSET_FROM_CENTER -= 0.010;  // kludge

        // the center of the diff port is moved from the center of the transistor:
        // - away from the center by half the gate length
        // - away from the center by the gate-to-contact distance
        // - away from the center by half the width of a contact
        double DIFF_PORT_OFFSET_FROM_CENTER     =
            (MIN_GATE_LENGTH / 2.0) + DCONT_TO_GATE + (getLayer("licon1").layerRules.minWidth / 2.0);

        for(String type : new String[] { "N", "P", "N-LVT" }) {
            String name = "Fet-"+type;
            Box           gateBox      = new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH);
            Primitive          fet          = new Primitive(SkyWater130.this, name, new PrimitiveGroup(SkyWater130.this), type.startsWith("N") ? "TRANMOS" : "TRAPMOS", gateBox);
            Primitive.Net  gate         = fet.new Net("gate");

            //
            // these four ports must appear in this specific order for NCC
            //
            Primitive.Port gate_left    =
                fet.new Port("gate-left",
                                 new ScaledBox(new Box(-GATE_PORT_OFFSET_FROM_CENTER, -MIN_GATE_LENGTH/2.0,
                                                       -GATE_PORT_OFFSET_FROM_CENTER,  MIN_GATE_LENGTH/2.0),
                                               new Box(-1,-1,-1,1)),
                                 gate,getLayer("poly"));
            Primitive.Port diff_top     =
                fet.new Port("diff-top",
                                 new ScaledBox(new Box(MIN_GATE_WIDTH, 0.0).shift(0, DIFF_PORT_OFFSET_FROM_CENTER)),
                                 getLayer("diff"));
            Primitive.Port gate_right   =
                fet.new Port("gate-right",
                                 new ScaledBox(new Box( GATE_PORT_OFFSET_FROM_CENTER, -MIN_GATE_LENGTH/2.0,
                                                        GATE_PORT_OFFSET_FROM_CENTER,  MIN_GATE_LENGTH/2.0),
                                               new Box(1,-1,1,1)),
                                 gate,getLayer("poly"));
            Primitive.Port diff_bot     =
                fet.new Port("diff-bot",
                                 new ScaledBox(new Box(MIN_GATE_WIDTH, 0.0).shift(0,-DIFF_PORT_OFFSET_FROM_CENTER)),
                                 getLayer("diff"));
            Primitive.Layer left_endcap = fet.new Rectangle(getLayer("poly"), gate_left,
                                                           new ScaledBox(new Box(POLY_ENDCAP - POLY_TO_DIFF,
                                                                                 MIN_GATE_LENGTH)
                                                                         .shift(-POLY_ENDCAP/2.0 -MIN_GATE_WIDTH/2.0 - POLY_TO_DIFF/2.0, 0),
                                                                         new Box(-1,-1,-1,1))
                                                           );
            Primitive.Layer right_endcap = fet.new Rectangle(getLayer("poly"), gate_right,
                                                            new ScaledBox(new Box(POLY_ENDCAP - POLY_TO_DIFF,
                                                                                  MIN_GATE_LENGTH)
                                                                          .shift(POLY_ENDCAP/2.0 + MIN_GATE_WIDTH/2.0 + POLY_TO_DIFF/2.0, 0),
                                                                          new Box(1,-1,1,1))
                                                            );
            Primitive.Layer gate_layer   = fet.new Rectangle(getLayer("gate"),
                                                            new ScaledBox(MIN_GATE_WIDTH + 2.0 * POLY_TO_DIFF,
                                                                          MIN_GATE_LENGTH));

            Primitive.Layer diff_top_layer = fet.new Rectangle(getLayer("diff"),
                                                              diff_top,
                                                              new ScaledBox(
                                                                            new Box(MIN_GATE_WIDTH,
                                                                                    SOURCE_DRAIN_LENGTH+MIN_GATE_LENGTH/2.0)
                                                                            .shift(0, SOURCE_DRAIN_LENGTH/2.0+MIN_GATE_LENGTH/4.0),
                                                                            new Box(-1,0,1,1)));

            Primitive.Layer diff_bot_layer = fet.new Rectangle(getLayer("diff"),
                                                              diff_bot,
                                                              new ScaledBox(
                                                                            new Box(MIN_GATE_WIDTH,
                                                                                    SOURCE_DRAIN_LENGTH+MIN_GATE_LENGTH/2.0)
                                                                            .shift(0,-SOURCE_DRAIN_LENGTH/2.0-MIN_GATE_LENGTH/4.0),
                                                                            new Box(-1,-1,1,0)));

            // difftap.c12: "0.18 min. enclosure of adj. sides of ""pdiff"" in core by nwell"
            // difftap.c8:  "0.15 min. enclosure of ""pdiff"" in core by nwell"
            Primitive.Layer well =
                type.startsWith("N") ? null :
                fet.new Rectangle(getLayer("nwell"),
                                  new ScaledBox(new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH + 2.0*SOURCE_DRAIN_LENGTH)
                                                .grow(0.18, 0.18)));

            fet.new Rectangle(getLayer(type.startsWith("N") ? "Electric-NAct" : "Electric-PAct"),
                              new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH + 2.0*SOURCE_DRAIN_LENGTH));
                                
            fet.new Rectangle(getLayer(type.startsWith("N") ? "nsdm" : "psdm"),
                              new ScaledBox(new Box(MIN_GATE_WIDTH,
                                                    MIN_GATE_LENGTH +
                                                    2.0*(DCONT_TO_GATE +
                                                         getLayer("licon1").layerRules.minWidth +
                                                         DIFF_ENCLOSURE_OF_DCONT))
                                            .grow(SELECT_SURROUND_ACT)));

            ScaledBox vtBox = new ScaledBox(new Box(MIN_GATE_WIDTH, MIN_GATE_LENGTH).grow(VT_SURROUND_GATE));
            if (type.equals("N-LVT")) fet.new Rectangle(getLayer("lvtn"), vtBox);
        }

        // Contact+Via Primitives //////////////////////////////////////////////////////////////////////////////
        
        // -1=DIFF, 0=POLY
        for(int i=-1; i<=numLayersPolyOrMetal()-2; i++) {
            TechLayer via        = getLayer(i==numLayersPolyOrMetal() ? "pad" :          i==-1 ? "licon1" : i==0 ? "licon1" : i==1 ? "mcon" : i==2 ? "via" : "via"+(i-1));
            TechLayer layerBelow = getLayer(i==-1 ? "diff" : i==0 ? "poly" : i==1 ? "li1" : ("met"+(i-1)));
            TechLayer layerAbove = i==numLayersPolyOrMetal() ? null : getLayer(i==-1 ? "li1"   : i==0 ? "li1" : ("met"+i));
            PrimitiveGroup nodeGroup = new PrimitiveGroup(SkyWater130.this);
            for(int style=0; style<2; style++) {
                Box nodebase = new Box(via.layerRules.minWidth, via.layerRules.minWidth);
                Box below    = new Box(via.layerRules.minWidth, via.layerRules.minWidth);
                Box above    = new Box(via.layerRules.minWidth, via.layerRules.minWidth);
                String stylename = style==0?"-X":style==1?"-P":"-FAIL";

                // style=0 means X-shaped (min-width metal on both layers, perpendicular)
                switch(i) {
                case -1: below = below.grow(0.060,0.040); above = above.grow(0.000,0.080); break; // DCONT (diff-licon1-li1)
                case  0: below = below.grow(0.080,0.050); above = above.grow(0.000,0.080); break; // PCONT (poly-licon1-li1)
                case  1: below = below.grow(0.000,0.000); above = above.grow(0.060,0.030); break; // Via   (li1-mcon-met1) (apparently no required enclosure by li)
                case  2: below = below.grow(0.085,0.055); above = above.grow(0.055,0.085); break; // Via2  (met1-via-met2)
                case  3: below = below.grow(0.040,0.085); above = above.grow(0.065,0.065); break; // Via3  (met2-via2-met3)
                case  4: below = below.grow(0.060,0.090); above = above.grow(0.065,0.065); break; // Via4  (met3-via3-met4)
                }
                  
                // style=1 means Parallel-shaped (min-width metal on both layers, parallel), so we swap the axes on one of the layers
                if (style==1) {
                    switch(i) {
                    case -1: case  0: below = below.swapAxes(); break;
                    case 1: continue;    // li1-mcon-met1 has no enclosure requirement for li1, so there is only one type of via cell
                    case 3: continue;    // met2-via2-met3 has square enclosure requirements on met3, so there is only one type of cell
                    case 4: continue;    // met3-via3-met4 has square enclosure requirements on met4, so there is only one type of cell
                    default:
                        if ((i%2) == 1) above = below.swapAxes();
                        else            below = below.swapAxes();
                    }
                }
                 
                String basename = i==-1 ? "DCont" : i==0 ? "PCont" : i==numLayersPolyOrMetal() ? "pad" : via.toString();
                String name     = basename+stylename;
                Primitive node  = new Primitive(SkyWater130.this, name, nodeGroup, "CONTACT", nodebase);
                
                // PCONT gets layer "npc" (nitride poly cut) with 0.045um enclosure (rule npcon.c6)
                if (i==0)
                    node.new Rectangle(getLayer("npc"),
                                       null, // no port
                                       new ScaledBox(new Box(via.layerRules.minWidth, via.layerRules.minWidth).grow(0.045, 0.045)));

                // ideal sizes for the above+below ports, if they could be different sizes
                Box portBelow = below.grow(-layerBelow.layerRules.minWidth / 2.0, -layerBelow.layerRules.minWidth / 2.0);
                Box portAbove = layerAbove==null ? portBelow :
                    above.grow(-layerAbove.layerRules.minWidth / 2.0, -layerAbove.layerRules.minWidth / 2.0);

                // for now take the intersection of the two
                Box portShape = new Box(Math.min(0, Math.max(portBelow.klx, portAbove.klx)),
                                        Math.min(0, Math.max(portBelow.kly, portAbove.kly)),
                                        Math.max(0, Math.min(portBelow.khx, portAbove.khx)),
                                        Math.max(0, Math.min(portBelow.khy, portAbove.khy)));

                Primitive.Net    net  = node.new Net("port");
                Primitive.Port   port = node.new Port("port", new ScaledBox(portShape), net, layerAbove, layerBelow);
                Primitive.Layer  nodeLayerAbove = layerAbove==null?null:node.new Rectangle(layerAbove, port, new ScaledBox(above));
                Primitive.Layer  nodeLayerBelow = layerBelow==null?null:node.new Rectangle(layerBelow, port, new ScaledBox(below));

                for(int j=0; j<=1; j++) {
                    TechLayer cutLayer = via;
                    if      (j==1 && i== 0)             cutLayer = getLayer("Electric-PCont");
                    else if (j==1 && i==-1)             cutLayer = getLayer("Electric-DCont");
                    if (cutLayer == null) continue;
                    node.new MultiCut(cutLayer, via.layerRules.minWidth, via.layerRules.minWidth,
                                      via.layerRules.minSpace, via.layerRules.minSpace, // FIXME
                                      new Box(via.layerRules.minWidth, via.layerRules.minWidth));
                }
            }
        }

        // Welltap Primitives //////////////////////////////////////////////////////////////////////////////
        
        for(boolean ptap : new boolean[] { true, false }) {
            Box nodeBase = new Box(0.15, 0.15);  // FIXME: what is the min-area requirement for taps?
            Primitive tapNode = new Primitive(SkyWater130.this, ptap?"ptap":"ntap", new PrimitiveGroup(SkyWater130.this), "CONTACT", nodeBase);
            Primitive.Port port = tapNode.new Port("port", new ScaledBox(nodeBase), getLayer("diff"));
            tapNode.new Rectangle(getLayer("diff"), port, new ScaledBox(nodeBase));

            // difftap.c10: "0.15 min. enclosure of ""ntap"" in core by nwell"
            if (ptap) tapNode.new Rectangle(getLayer("nwell"), new ScaledBox(nodeBase.grow(0.15)));
            
            // nsd.c5a:      0.13 min. enclosure of n+ tap in core by nsdm
            // psd.c5b:      0.12 min. enclosure of p+ tap in core by psdm
            tapNode.new Rectangle(getLayer(ptap?"psdm":"nsdm"), new ScaledBox(ptap ? nodeBase.grow(0.13) : nodeBase.grow(0.12)));
        }

        super.dump(pw);
    }

    @Override protected void header(IndentingPrintWriter pw) throws IOException {
        pw.println();
        pw.println();
        pw.println("<!-- DO NOT EDIT THIS FILE DIRECTLY - - it is generated programmatically -->");
        pw.println();
        pw.println();
        pw.println("<!-- Copyright 2020 Western Semiconductor Corporation -->");
        pw.println();
        pw.println("<!-- Licensed under the Apache License, Version 2.0 (the \"License\"); -->");
        pw.println("<!-- you may not use this file except in compliance with the License. -->");
        pw.println("<!-- You may obtain a copy of the License at -->");
        pw.println();
        pw.println("<!-- http://www.apache.org/licenses/LICENSE-2.0 -->");
        pw.println();
        pw.println("<!-- Unless required by applicable law or agreed to in writing, software -->");
        pw.println("<!-- distributed under the License is distributed on an \"AS IS\" BASIS, -->");
        pw.println("<!-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. -->");
        pw.println("<!-- See the License for the specific language governing permissions and -->");
        pw.println("<!-- limitations under the License. -->");
        pw.println();
        pw.println();
    }

    
    @Override protected void dumpMenuPalette(IndentingPrintWriter pw) throws IOException {
        pw.println("<menuPalette numColumns='3'>");
        pw.adjustIndentation(4);
        pw.println("<menuBox><menuText>Pure</menuText></menuBox>");
        pw.println("<menuBox><menuText>Misc.</menuText></menuBox>");
        pw.println("<menuBox><menuText>Cell</menuText></menuBox>");

        pw.println("<menuBox>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("  <menuNodeInst protoName='nwell' function='UNKNOWN'>");
        pw.println("    <menuNodeText size='4' text='nwell'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("</menuBox>");

        pw.println("<menuBox>");
        pw.println(" <menuNodeInst protoName='ptap' function='CONTACT'><menuNodeText size='4' text='ptap'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println(" <menuNodeInst protoName='ntap' function='CONTACT'><menuNodeText size='4' text='ntap'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("</menuBox>");

        pw.println("<menuBox>");
        pw.println("  <menuNodeInst protoName='Fet-N'        rotation='900' function='TRANMOS'><menuNodeText size='4' text='N'/></menuNodeInst>");
        pw.println("  <menuNodeInst protoName='Fet-N-LVT'    rotation='900' function='TRANMOS'><menuNodeText size='4' text='N-LVT'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("  <menuNodeInst protoName='Fet-P'        rotation='900' function='TRAPMOS'><menuNodeText size='4' text='P'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("</menuBox>");

        pw.println("<menuBox>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println(" <menuNodeInst protoName='nsdm' function='UNKNOWN'><menuNodeText size='4' text='nsdm'/></menuNodeInst>");
        pw.println(" <menuNodeInst protoName='psdm' function='UNKNOWN'><menuNodeText size='4' text='psdm'/></menuNodeInst>");
        pw.println("</menuBox>");
        pw.println("<menuBox>");
        pw.println("</menuBox>");

        for(int i=lowestMetalLayerOrdinal()-2; i<=numLayersPolyOrMetal()-lowestMetalLayerOrdinal()+1; i++) {

            String layerName =
                i==lowestMetalLayerOrdinal()-2 ? "diff" :
                i==lowestMetalLayerOrdinal()-1 ? "poly" :
                i==0 ? "li1" :
                "met"+i;

            pw.println("<menuBox>");
            pw.adjustIndentation(4);
            pw.println("<menuArc>"+layerName+"</menuArc>");
            pw.adjustIndentation(-4);
            pw.println("</menuBox>");

            pw.println("<menuBox>");
            pw.adjustIndentation(4);
            pw.println("<menuNode>"+layerName+"</menuNode>");
            pw.println("<menuNodeInst protoName='"+layerName+"-Pin' function='PIN'/>");
            pw.println("<menuNodeInst protoName='"+layerName+"-Minarea' function='PIN'/>");
            pw.adjustIndentation(-4);
            pw.println("</menuBox>");

            pw.println("<menuBox>");
            pw.adjustIndentation(4);
            if (i==lowestMetalLayerOrdinal()-2) {
                pw.println("<menuNodeInst protoName='DCont-X' rotation='900' function='CONTACT'/>");
                pw.println("<menuNodeInst protoName='DCont-P' rotation='900' function='CONTACT'/>");
            } else if (i==lowestMetalLayerOrdinal()-1) {
                pw.println("<menuNode>PCont-X</menuNode>");
                pw.println("<menuNode>PCont-P</menuNode>");
            } else if (i==lowestMetalLayerOrdinal()) {
                pw.println("<menuNode>mcon-X</menuNode>");
                pw.println("<menuNode>mcon-P</menuNode>");
            } else if (i==lowestMetalLayerOrdinal()+1) {
                pw.println("<menuNode>via-X</menuNode>");
                pw.println("<menuNode>via-P</menuNode>");
            } else {
                pw.println("<menuNode>via"+i+"-X</menuNode>");
                pw.println("<menuNode>via"+i+"-P</menuNode>");
            }
            pw.adjustIndentation(-4);
            pw.println("</menuBox>");
        }

        pw.adjustIndentation(-4);
        pw.println("</menuPalette>");
    }

    public static void main(String[] s) throws Exception {
        new SkyWater130().dump(new IndentingPrintWriter(new IndentingWriter(new OutputStreamWriter(System.out))));
    }
}
