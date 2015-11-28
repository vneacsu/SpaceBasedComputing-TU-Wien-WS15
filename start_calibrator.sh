#!/bin/bash

if [ "$#" -ne 0 ]; then
    echo "Usage: $(basename $0)"
    exit 0
fi

./src/calibrate-robot/build/install/calibrate-robot/bin/calibrate-robot