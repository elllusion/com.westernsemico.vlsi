Electric technology XML files ("Electric PDKs") produced by Western
Semiconductor Corporation.

Currently due to NDA issues we are only able to redistribute the
SkyWater130 PDK.  Our Electric PDKs for TSMC65, TSMC28, ST65, UMC180,
UMC65, GF28, and GF14 are under NDA and we cannot release them.

Quick Start:

1. Check out this repository, run "make skywater130.xml".
   Alternately, download the pre-generated skywater130.xml from the
   "releases" section
   [here](https://gitlab.com/westernsemico/releases/-/raw/master/electric-skywater130-demo-contacts.png)

2.  Download electricBinary-9.07.jar from https://staticfreesoft.com/productsFree.html

3.  Run java -jar electricBinary-9.07.jar

4.  Go to File -> Preferences -> Display -> Display Control and choose
    "Layer Display Algorithm".  If you don't do this you will be
    unable to see certain layers, like "licon1".

5.  Go to File -> Preferences -> Technologies -> Added Technologies.
    Click "Add" and choose skywater130.xml.  **When the "Saving
    Project Preferences" dialog comes up, choose "write proj settings
    file".** Quit electric and start it up again.

At this point you should be able to import GDS files from the
SkyWater130 PDK using File -> Import -> GDS Stream.

**Note:** Electric's default text size is very large.  You may need to
press control-minus (or command-minus on a mac) a few times to shrink
the text to a reasonable size.

You can also try the demo library
[sk130.jelib](https://gitlab.com/westernsemico/releases/-/raw/master/sk130.jelib)
in the releases section.  **When opening the library, if you see a
"Project Preferences Reconciliation" dialog, choose "Use All Current
Settings".** DRC and LVS (aka NCC) should work for the demo library,
as well as 3D view and GDS export.

![3D view of pads](https://gitlab.com/westernsemico/releases/-/raw/master/skywater130-driver.png)

![demo contacts](https://gitlab.com/westernsemico/releases/-/raw/master/electric-skywater130-demo-contacts.png)

![skywater130-lvs.png](https://gitlab.com/westernsemico/releases/-/raw/master/skywater130-lvs.png)

![skywater130-metalstack.png](https://gitlab.com/westernsemico/releases/-/raw/master/skywater130-metalstack.png)

![3D view of standard cell](https://gitlab.com/westernsemico/releases/-/raw/master/electric-skywater130-3d.png)

