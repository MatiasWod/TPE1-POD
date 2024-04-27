#!/bin/bash

PATH_TO_CODE_BASE=`pwd`

tar -xf ./server/target/tpe1-g10-server-2024.1Q-bin.tar.gz
tar -xf ./client/target/tpe1-g10-client-2024.1Q-bin.tar.gz

cd ./tpe1-g10-client-2024.1Q/

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addSector -Dsector=C
echo " "
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=30
echo " "
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=manifest -DinPath=../manifest.csv
