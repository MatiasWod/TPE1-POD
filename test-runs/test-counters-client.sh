tar -xf ./server/target/tpe1-g10-server-2024.1Q-bin.tar.gz
tar -xf ./client/target/tpe1-g10-client-2024.1Q-bin.tar.gz

cd ./tpe1-g10-client-2024.1Q/

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addSector -Dsector=C

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=5

sh adminClient.sh -DserverAddress=127.0.0.1:50051 -Daction=manifest -DinPath=../test-runs/manifestForCounters.csv

sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights=aa012 -Dairline=americanairlines -DcounterCount=5
sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=assignCounters -Dsector=E -Dflights=aa012 -Dairline=americanairlines -DcounterCount=2
sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights=aa012 -Dairline=tataairline -DcounterCount=2
sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights=aa013 -Dairline=tataairline -DcounterCount=7
sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights=aa013 -Dairline=tataairline -DcounterCount=7

sh counterClient.sh -DserverAddress=127.0.0.1:50051 -Daction=freeCounters -Dsector=C -DcounterFrom=1 -Dairline=americanairlines