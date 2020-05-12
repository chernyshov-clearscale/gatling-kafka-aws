# Gatling script to load valid UBX messages to kafka topic

This script can be run locally or deployed as docker container into kafka cluster.
Local execution should be used for script debugging only as network overhead limits throughput.

### Input parameters
Script accepts following required parameters (in the listed order):
1. rate - number of messages to produce in a second (TPS)
2. period - time to run script in seconds
3. topic - kafka topic name
4. bootstrap.servers - kafka bootstrap servers

### Local run
Configuration parameters passed in command line with -D.

Execute maven test command in the root of the project.

Example to publish "1" message for "2" seconds on topic "test" on dev cluster
 ```
mvn gatling:test -Dgatling.simulationClass=nl.codecontrol.gatling.simulations.UbxMessageKafkaSimulation -Drate="1" -Dperiod="1" -Dtopic="test" -Dbootstrap.servers="b-2.journey-dev-msk.xkesoi.c4.kafka.us-east-1.amazonaws.com:9092,b-3.journey-dev-msk.xkesoi.c4.kafka.us-east-1.amazonaws.com:9092,b-1.journey-dev-msk.xkesoi.c4.kafka.us-east-1.amazonaws.com:9092"
```

### Kubernetes run
Configuration parameters set in producer/deployment.yaml file (args)

Execute kubernetes deployment:
```
kubectl apply -f producer/deployment.yaml
```

**Remove container after testing finished** as it's automatically restarted after script finished.
So script will run in infinitely if container not removed from cluster.
```
kubectl apply -f producer/deployment.yaml
```