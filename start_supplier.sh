#!/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Usage: $(basename $0) componentType quantity interval"
    exit 0
fi

export JAVA_OPTS="-DcomponentType=$1 -Dquantity=$2 -Dinterval=$3"

./src/supply-robot/build/install/supply-robot/bin/supply-robot