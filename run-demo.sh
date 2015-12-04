#!/usr/bin/env bash

pids=""

function cleanup {
	echo Shutting down processes: $pids
	kill $pids
}

trap cleanup SIGINT SIGTERM EXIT


export JVM_OPTS="-DrepoStrategy=$1"

./src/factory-ui/build/install/factory-ui/bin/factory-ui > /dev/null &

sleep 5


ASSEMBLY_ROBOT_OPTS="-DrobotId=Assembler_1" \
./src/assembly-robot/build/install/assembly-robot/bin/assembly-robot > /dev/null &
pids="$pids $!"

ASSEMBLY_ROBOT_OPTS="-DrobotId=Assembler_2" \
./src/assembly-robot/build/install/assembly-robot/bin/assembly-robot > /dev/null &
pids="$pids $!"


CALIBRATE_ROBOT_OPTS="-DrobotId=Calibrator_1" \
./src/calibrate-robot/build/install/calibrate-robot/bin/calibrate-robot > /dev/null &
pids="$pids $!"

CALIBRATE_ROBOT_OPTS="-DrobotId=Calibrator_2" \
./src/calibrate-robot/build/install/calibrate-robot/bin/calibrate-robot > /dev/null &
pids="$pids $!"


LOGISTICS_ROBOT_OPTS="-DrobotId=Logistician_1" \
./src/logistics-robot/build/install/logistics-robot/bin/logistics-robot > /dev/null &
pids="$pids $!"

LOGISTICS_ROBOT_OPTS="-DrobotId=Logistician_2" \
./src/logistics-robot/build/install/logistics-robot/bin/logistics-robot > /dev/null &
pids="$pids $!"



SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_1 -DcomponentType=casing -Dquantity=2 -Dinterval=5000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_2 -DcomponentType=control-unit -Dquantity=3 -Dinterval=7000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_3 -DcomponentType=engine -Dquantity=2 -Dinterval=3000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_4 -DcomponentType=rotor -Dquantity=3 -Dinterval=4000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"


sleep 30

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_5 -DcomponentType=casing -Dquantity=10 -Dinterval=5000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_6 -DcomponentType=control-unit -Dquantity=10 -Dinterval=7000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_7 -DcomponentType=engine -Dquantity=32 -Dinterval=3000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

SUPPLY_ROBOT_OPTS="-DrobotId=Supplier_8 -DcomponentType=rotor -Dquantity=35 -Dinterval=4000" \
./src/supply-robot/build/install/supply-robot/bin/supply-robot > /dev/null &
pids="$pids $!"

wait $pids
