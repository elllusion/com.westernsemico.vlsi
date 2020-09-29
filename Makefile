
skywater130.xml: com.westernsemico.vlsi.jar
	java -cp com.westernsemico.vlsi.jar com.westernsemico.vlsi.tech.SkyWater130 > $@

com.westernsemico.vlsi.jar: $(shell find src -name \*.java)
	mkdir -p build
	javac -d build $(shell find src -name \*.java)
	cd build; jar cvf ../$@ .

