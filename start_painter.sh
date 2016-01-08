#!/bin/bash

if [ "$#" -ne 0 ]; then
    echo "Usage: $(basename $0)"
    exit 0
fi

./src/paint-robot/build/install/paint-robot/bin/paint-robot