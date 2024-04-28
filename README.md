# TPE1-POD

---

## Contributors

---

| Alumno                | Legajo | Mail                  |
|-----------------------|--------|-----------------------|
| Abancens, Alberto     | 62581  | aabancens@itba.edu.ar |
| Arnaude, Juan Segundo | 62184  | jarnaude@itba.edu.ar  |
| Canevaro, Bautista    | 62179  | bcanevaro@itba.edu.ar |
| Wodtke, Matías        | 62098  | mwodtke@itba.edu.ar   |

<hr>

* [1. Prerequisites](#1-prerequisites)
* [2. Compiling](#2-compiling)
* [3. Executing Project](#3-executing-project)
    * [3.1. Server](#31-server)
    * [3.2. Admin Client](#32-admin-client)
        * [3.2.1. Add a sector](#321-add-a-sector)
        * [3.2.2. Add Counters](#322-add-counters)
        * [3.2.3. Load expected passenger](#323-load-expected-passenger)
    * [3.3. Counter Client](#33-counter-client)
        * [3.3.1. Print sectors](#331-print-sectors)
        * [3.3.2. Print counters](#332-print-counters)
        * [3.3.3. Assign counters](#333-assign-counters)
        * [3.3.4. Free counters](#334-free-counters)
        * [3.3.5. Check-in for each counter](#335-check-in-for-each-counter)
        * [3.3.6. Print pending assignments](#336-print-pending-assignments)
    * [3.4. Passenger Client](#34-passenger-client)
        * [3.4.1. Prints passenger check-in status](#341-prints-passenger-check-in-status)
        * [3.4.2. Enter counter range queue](#342-enter-counter-range-queue)
    * [3.5. Events Client](#35-events-client)
        * [3.5.1. Register airline to be notified](#351-register-airline-to-be-notified)
        * [3.5.2. Cancel airline registration to be notified](#352-cancel-airline-registration-to-be-notified)
    * [3.6. Query Client](#36-query-client)
        * [3.6.1. Print counters' status filtering by sector](#361-print-counters-status-filtering-by-sector)
        * [3.6.2. Print completed check-in filtering by sector and airline](#362-print-completed-check-in-filtering-by-sector-and-airline)      

<hr>

## 1. Prerequisites

In order to properly run both server and client applications, it is compulsory to have installed:

* Maven
* Java 19

---

## 2. Compiling

To compile the project and get all executables, `cd` into the root of the project, and run the following command:

```Bash
mvn clean package
```

This will create two `.tar.gz` files, that contain all the files necessary to run the clients and the server. They are located at:
* **Client**: `./client/target/tpe1-g10-client-2024.1Q-bin.tar.gz`
* **Server**: `./server/target/tpe1-g10-server-2024.1Q-bin.tar.gz`

---

## 3. Executing Project

Unzip both files created previously by running:

```Bash
tar -xf <file.tar.gz>
```

Then, give all executables permission to be executed:

```Bash
chmod u+x ./client/*Client ./server/TPE1-POD
```

---
### 3.1. Server

Must be running for the clients to work. Once it's stopped, all stored data is lost.

---

### 3.2. Admin Client

> 🚨 The current working directory **must** be `./client`.

Three operations are supported:

#### 3.2.1. Add a sector
Adds a sector using the name given "sectorName" if it doesn't already exist.

```Bash
./adminClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=addSector  -Dsector=sectorName 
```

#### 3.2.2. Add Counters
Adds counters to a sector, if the sector exists.

```Bash
./adminClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=addCounters  -Dsector=sectorName  -Dcounters=countersAmount 
```

#### 3.2.3. Load expected passenger
Loads passengers by using data given in a csv file, this file (manifest.csv) **must** contain the following fields separated by a `;`.
* Booking
* Flight
* Airline

See below an example:

```Text
booking;flight;airline
ABC123;AC987;AirCanada
XYZ234;AA123;AmericanAirlines
```

```Bash
./adminClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=manifest -DinPath=../manifest.csv
```

---
### 3.3. Counter Client

> 🚨 The current working directory **must** be `./client`.

Six operations are supported:

#### 3.3.1. Print sectors
Lists all current sectors and their counter ranges.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=listSectors
```

#### 3.3.2. Print counters
Lists given counter range giving the flights assigned to them or no information if they are free.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=listCounters  -DcounterFrom=fromVal  -DcounterTo=toVal
```

#### 3.3.3. Assign counters
Assigns counters to the given flights or leaves them pending if there are no available counters.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY -Daction=assignCounters  -Dsector=sectorName  -Dflights=flightCode1|flightCode2|...  -Dairline=airlineName  -DcounterCount=counterCount
```

#### 3.3.4. Free counters
Frees all assigned counters starting from `fromVal`.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=freeCounters  -Dsector=sectorName  -DcounterFrom=fromVal  -Dairline=airlineName
```

#### 3.3.5. Check-in for each counter
Checks in a passenger for each counter starting from `fromVal`.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=checkinCounters  -Dsector=sectorName  -DcounterFrom=fromVal  -Dairline=airlineName
```

#### 3.3.6. Print pending assignments
Lists all flights from `sectorName` pending to be assigned to a counter.

```Bash
./counterClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=listPendingAssignments  -Dsector=sectorName
```

---
### 3.4. Passenger Client

> 🚨 The current working directory **must** be `./client`.

Three operations are supported:

#### 3.4.1. Prints passenger check-in status
Prints passenger check-in status given his `bookingCode`.

```Bash
./passengerClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=passengerStatus  -Dbooking=bookingCode
```

#### 3.4.2. Enter counter range queue
Enter counter range queue.

```Bash
./passengerClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=passengerCheckin  -Dbooking=flightCode  -Dsector=sectorName  -Dcounter=counterNumber
```

#### 3.4.3. Print check-in status
Print passenger's check-in status given his `bookingCode`.

```Bash
./passengerClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=passengerStatus  -Dbooking=bookingCode
```

---
### 3.5. Events Client

> 🚨 The current working directory **must** be `./client`.

Two operations are supported:

#### 3.5.1. Register airline to be notified
Register an airline to be notified of the events related to it.

```Bash
./eventsClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=register  -Dairline=airlineName
```

#### 3.5.2. Cancel airline registration to be notified
Cancel an airline registration to be notified of the events related to it.

```Bash
./eventsClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=unregister -Dairline=airlineName
```

---
### 3.6. Query Client

> 🚨 The current working directory **must** be `./client`.

Two operations are supported:

#### 3.6.1. Print counters' status filtering by sector
Print counters' current status filtering by sector given `sectorName` or simply prints all of them if not given.

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=queryCounters  -DoutPath=../query1.txt  -Dsector=sectorName
```

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=queryCounters  -DoutPath=../query1.txt
```

#### 3.6.2. Print completed check-in filtering by sector and airline
Print completed check-in filtering by sector and airline, if given.

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=checkins  -DoutPath=../query2.txt
```

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=checkins  -DoutPath=../query2.txt  -Dsector=sectorName
```

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=checkins  -DoutPath=../query2.txt  -Dairline=airlineName
```

```Bash
./queryClient -DserverAddress=XX.XX.XX.XX:YYYY  -Daction=checkins  -DoutPath=../query2.txt  -Dsector=sectorName  -Dairline=airlineName
```