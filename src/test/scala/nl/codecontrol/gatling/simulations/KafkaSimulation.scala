package nl.codecontrol.gatling.simulations

import java.time.Clock
import java.util.UUID

import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._
import com.github.mnogu.gatling.kafka.Predef._
import com.google.common.primitives.{Bytes, Longs}

class KafkaSimulation extends Simulation {

  // Key to distribute messages across partitions
  val keys = Array.fill(10){UUID.randomUUID().toString}
  // TODO size of array should be parametrized
  val stubData = Array.fill(50000){0.toByte}
  val clock = Clock.systemUTC()
//  Array(1, 2).groupBy(k => k).map(f => (f._1, 0))
//  var messageCounter: Map[String, Long] = keys.groupBy(k => k).map(f => (f._1, 0L))
  val messageCounter = collection.mutable.Map(keys.groupBy(k => k).map(f => (f._1, 0L)).toSeq: _*)

  val kafkaConf: KafkaProtocol = kafka
    // Kafka topic name
    // TODO should be parametrized
    .topic("test")
    // Kafka producer configs
    .properties(
    Map(
      // UNCOMMENT THE FOLLOWING LINES TO ENABLE SSL. DISABLED FOR LOCAL TESTING.
      /*
      "security.protocol"->"SSL",
      // Path to jks certificate on Docer container
      "ssl.truststore.location"->"/usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts",
      */

      ProducerConfig.ACKS_CONFIG -> "1",
      // TODO should be parametrized
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",

      // in most cases, StringSerializer or ByteArraySerializer
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.ByteArraySerializer"))

  // TODO here is the possible way to pass parameters
  println(System.getProperty("environment"))

  val random = scala.util.Random
  val orderRefs = Iterator.continually({
    // Random number will be accessible in session under variable "OrderRef"
    val key = keys(random.nextInt(keys.length))
    val counter = messageCounter(key)
    Map(
      "value" -> {
        val next = Bytes.concat(
          Longs.toByteArray(counter),
          Longs.toByteArray(clock.instant().toEpochMilli),
          // payload data
          // TODO size of array should be parametrized
          stubData
        ).map(_.toByte)
        messageCounter(key) = counter + 1
        //        messageCounter+=1
        next
      },
      "key" -> {
        key
      }
    )
  })

  val scn = scenario("Kafka Test")
    .feed(orderRefs)
    .exec(kafka("request").send[String, Array[Byte]]("${key}", "${value}"))

  // TODO should be parametrized
  setUp(scn.inject(constantUsersPerSec(3) during(5 seconds)))
    .protocols(kafkaConf)

}
