#!/bin/bash

FILE=mozartspaces-dist-2.3-SNAPSHOT-r15239-all-with-dependencies.jar

cd infra/

if [ ! -f ${FILE} ];
then
    wget http://www.mozartspaces.org/2.3-SNAPSHOT/download/mozartspaces-dist-2.3-SNAPSHOT-r15239-all-with-dependencies.jar
fi

java -cp ${FILE} org.mozartspaces.core.Server 4242
