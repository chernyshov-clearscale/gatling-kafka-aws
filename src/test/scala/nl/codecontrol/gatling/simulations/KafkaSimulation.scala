package nl.codecontrol.gatling.simulations

//import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._
import com.github.mnogu.gatling.kafka.Predef._

class KafkaSimulation extends Simulation {
  val kafkaConf: KafkaProtocol = kafka
    // Kafka topic name
    .topic("test1")
    // Kafka producer configs
    .properties(
    Map(
      "security.protocol"->"SSL",
      "ssl.truststore.location"->"/tmp/kafka.client.truststore.jks",
//      "ssl.keystore.location"->"/tmp/kafka.client.truststore.jks",
      ProducerConfig.ACKS_CONFIG -> "1",
      // list of Kafka broker hostname and port pairs
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "b-1.awskafkatutorialcluste.bampes.c3.kafka.us-east-2.amazonaws.com:9094,b-3.awskafkatutorialcluste.bampes.c3.kafka.us-east-2.amazonaws.com:9094,b-2.awskafkatutorialcluste.bampes.c3.kafka.us-east-2.amazonaws.com:9094",

      // in most cases, StringSerializer or ByteArraySerializer
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Kafka Test")
    .exec(
      kafka("request")
        // message to send
        .send[String]("bar"))

  setUp(
    scn
      .inject(constantUsersPerSec(200) during(5 seconds)))
    .protocols(kafkaConf)
}
