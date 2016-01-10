# Drone factory - Space Based Computing WS 2015 - TU WIEN

Use `./gradlew installDist` to build the binaries.

The required functionality is implemented by means of two different middleware technologies
( *space based* and *message oriented*). You can choose one of these technologies by setting the property `repoStrategy`
to either `spaceBased` (default value) or `xBased`.
All components read the value of this property at runtime,
so please make sure you set it properly and that it is consistent among the components.

Additionally, if you want to use the message oriented technology please install [docker](https://www.docker.com/)
and then start a docker container via `start-rabbitmq.sh`.

In order to run a demo (involving all the components) you only need to run the `run-demo.sh` script.

## UI

Make sure that you start this component **before** the robots.

`./src/factory-ui/build/install/factory-ui/bin/factory-ui`

## Supply-Robot

This robot takes 3 input parameters:

* componentType (value: case/controlUnit/casing/controlUnit)
* quantity
* interval (millis)

Start a robot that supplies 3 rotors, each at an interval of 1000ms:

`JAVA_OPTS="-DcomponentType=rotor -Dquantity=3 -Dinterval=1000" ./src/supply-robot/build/install/supply-robot/bin/supply-robot`

## Paint-Robot

This robot takes no input parameters.

Start an paint-robot:

`./src/paint-robot/build/install/paint-robot/bin/paint-robot`

## Assembly-Robot

This robot takes no input parameters.

Start an assembly-robot:

`./src/assembly-robot/build/install/assembly-robot/bin/assembly-robot`

## Calibrate-Robot

This robot takes no input parameters.

Start a calibrate-robot:

`./src/calibrate-robot/build/install/calibrate-robot/bin/calibrate-robot`

## Logistics-Robot

This robot takes 1 input parameter:

* admissibleRange (default: 15)

Start a logistics-robot with admissibility range 5:
 
`JAVA_OPTS=-DadmissibleRange=5 ./src/logistics-robot/build/install/logistics-robot/bin/logistics-robot`

**Note**: all robots take an optional argument `robotId`

## Our team

Valentin-Mihai Neacsu (1127157)

Mihai-Alexandru Lepadat (1127960)