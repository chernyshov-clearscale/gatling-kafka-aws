#!/bin/bash
# Running performance test
# TODO -D<variable_name> could be used to pass parameters
mvn -X gatling:test -Dgatling.simulationClass=nl.codecontrol.gatling.simulations.UbxMessageKafkaSimulation -Drate="$1" -Dperiod="$2" -Dtopic="$3" -Dbootstrap.servers="$4"
