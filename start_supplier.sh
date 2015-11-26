#!/bin/bash

export SUPPLY_ROBOT_OPTS="-DcomponentType=$1 -Dquantity=$2 -Dinterval=$3"

./src/supply-robot/build/install/supply-robot/bin/supply-robot