# Drone factory - Space Based Computing WS 2015 - TU WIEN

Before starting any robot you should start the space server (i.e. *main* method of *server* module).

## Supply-Robot

This robot takes 4 input parameters:

* repoStrategy (values: spaceBased/xBased) - *optional* (default: spaceBased)
* componentType (values: case/controlUnit/casing/controlUnit)
* quantity
* interval (millis)

E.g. Start a robot that supplies 3 rotors, each at an interval of 1000ms:
`JAVA_OPTS="-DcomponentType=controlUnit -Dquantity=3 -Dinterval=1000" supply-robot`