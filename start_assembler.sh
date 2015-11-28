#!/bin/bash

if [ "$#" -ne 0 ]; then
    echo "Usage: $(basename $0)"
    exit 0
fi

./src/assembly-robot/build/install/assembly-robot/bin/assembly-robot