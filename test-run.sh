#!/bin/bash

PATH_TO_CODE_BASE=`pwd`

tar -xf ./server/target/tpe1-g10-server-2024.1Q-bin.tar.gz
tar -xf ./client/target/tpe1-g10-client-2024.1Q-bin.tar.gz

cd ./tpe1-g10-client-2024.1Q/

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addSector -Dsector=C
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=5
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=manifest -DinPath=../manifest.csv
sh counterClient.sh -DserverAddress=localhost:50051 -Daction=assignCounters -Dsector=C -Dflights=aa012 -Dairline=americanairlines -DcounterCount=3

sh passengerClient.sh -DserverAddress=localhost:50051 -Daction=passengerCheckin -Dbooking=ghi011 -Dsector=C -Dcounter=1
sh passengerClient.sh -DserverAddress=localhost:50051 -Daction=passengerCheckin -Dbooking=ghi012 -Dsector=C -Dcounter=1
#sh passengerClient.sh -DserverAddress=localhost:50051 -Daction=passengerCheckin -Dbooking=ghi013 -Dsector=C -Dcounter=1
sh passengerClient.sh -DserverAddress=localhost:50051 -Daction=passengerCheckin -Dbooking=ghi014 -Dsector=C -Dcounter=1
sh passengerClient.sh -DserverAddress=localhost:50051 -Daction=passengerCheckin -Dbooking=ghi015 -Dsector=C -Dcounter=1

sh counterClient.sh -DserverAddress=localhost:50051 -Daction=checkinCounters -Dsector=C -DcounterFrom=1 -Dairline=americanairlines
