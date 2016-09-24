
all: master-classes zone-classes client-classes

master-classes:
	mkdir -p build/master/bin
	javac -sourcepath ServerMaster/src -d build/master/bin ServerMaster/src/cl/utfsm/master/Master.java

zone-classes:
	mkdir -p build/zone/bin
	javac -sourcepath ServerZone/src -d build/zone/bin ServerZone/src/cl/utfsm/zone/Zone.java

client-classes:
	mkdir -p build/client/bin
	javac -sourcepath ServerClient/src -d build/client/bin ServerClient/src/cl/utfsm/client/Client.java

run-master: master-classes
	java -cp build/master/bin cl.utfsm.master.Master

run-zone: zone-classes
	java -cp build/zone/bin cl.utfsm.zone.Zone

run-client: client-classes
	java -cp build/client/bin cl.utfsm.client.Client


clean:
	rm -rf build

