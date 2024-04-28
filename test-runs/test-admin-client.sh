tar -xf ./server/target/tpe1-g10-server-2024.1Q-bin.tar.gz
tar -xf ./client/target/tpe1-g10-client-2024.1Q-bin.tar.gz

cd ./tpe1-g10-client-2024.1Q/

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addSector -Dsector=C
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addSector -Dsector=C

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=3
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters="-3"
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=D -Dcounters=3

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=manifest -DinPath=../test-runs/manifest.csv
sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=manifest -DinPath=../test-runs/manifest2.csv

