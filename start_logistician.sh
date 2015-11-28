#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $(basename $0) admissibleRange"
    exit 0
fi

export SUPPLY_ROBOT_OPTS="-DadmissibleRange=$1"

./src/logistics-robot/build/install/logistics-robot/bin/logistics-robot