package nl.codecontrol.gatling.simulations

import java.time.Clock
import java.util.UUID

import com.github.mnogu.gatling.kafka.Predef._
import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._
import scala.util.Random

class UbxMessageKafkaSimulation extends Simulation {

  // Key to distribute messages across partitions
  val key = System.getProperty("key", "SomeKey")
  // TODO size of array should be parametrized
  val clock = Clock.systemUTC()
  var messageCounter: Long = 0L

  val kafkaConf: KafkaProtocol = kafka
    // Kafka topic name
    // TODO should be parametrized
    .topic("test1")
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
          "org.apache.kafka.common.serialization.StringSerializer"))

  // TODO here is the possible way to pass parameters
  println(System.getProperty("environment"))


  val ubxMessages = Iterator.continually(
    // Random number will be accessible in session under variable "OrderRef"
    Map("value" -> {
      val x1Id = UUID.randomUUID().toString
      val low = 99990
      val high = 99999
      val ubxRequest =
        s"""
           |{
           |    "headers": [{
           |            "name": "account_id",
           |            "value": "${Random.nextInt(high - low) + low}"
           |        }, {
           |            "name": "source_endpoint_id",
           |            "value": "${Random.nextInt(high - low) + low}"
           |        }
           |    ],
           |    "eventbatch": [{
           |            "source": "IBM Digital Analytics_IBM DA Deployment - Manual",
           |            "x1Id": "${x1Id}",
           |            "version": "1",
           |            "provider": "IBM",
           |            "channel": "WEB",
           |            "identifiers": [{
           |                    "name": "cookieId",
           |                    "value": "90015377021414745477202",
           |                    "isOriginal": true,
           |                    "endpointId": 279271
           |                }
           |            ],
           |            "events": [{
           |                    "code": "ibmpageviewEntryPage-${clock.instant().toEpochMilli}",
           |                    "timestamp": "${clock.instant().toString}",
           |                    "namespace": "com.ibm.commerce.ubx",
           |                    "version": "1",
           |                    "attributes": [{
           |                            "name": "subChannel",
           |                            "value": "WEB",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "deviceType",
           |                            "value": "",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "vendor",
           |                            "value": "",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "model",
           |                            "value": "",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "OS",
           |                            "value": "MICROSOFT",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "versionOS",
           |                            "value": "WINDOWS7",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "browserName",
           |                            "value": "CHROME",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "browserVersion",
           |                            "value": "12.0.712.0",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "interactionId",
           |                            "value": "00001022701157516453972230000001",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "daAssignedSessionId",
           |                            "value": "1434014953546101915",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "eventName",
           |                            "value": "Entry Page",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "clientId",
           |                            "value": "30000001",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "pageID",
           |                            "value": "PRODUCT: LEATHER HIGH-BACK OFFICE CHAIR (FUOF-0301)",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "pageURL",
           |                            "value": "http://retail-demo.coremetrics.com/LiveDemo/product?catalog_id=1&category_id=2&prod_id=4",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "referralURL",
           |                            "value": "http://www.bing.com/?q=lounge chair VT Living",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "category",
           |                            "value": "OFFICE CHAIRS",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "ip",
           |                            "value": "169.53.31.21",
           |                            "type": "string",
           |                            "isSync": false
           |                        }, {
           |                            "name": "marketingSource",
           |                            "value": "Natural Search",
           |                            "type": "string",
           |                            "isSync": false
           |                        }
           |                    ],
           |                    "x1Id": null,
           |                    "source": null,
           |                    "provider": "IBM"
           |                }
           |            ]
           |        }
           |    ]
           |}
           |""".stripMargin
      println(x1Id)
      //println(ubxRequest)
      ubxRequest
    })
  )

  val scn = scenario("Kafka Test")
    .feed(ubxMessages)
    .exec(kafka("request").send[String, String](key, "${value}"))

  // TODO should be parametrized
  setUp(scn.inject(constantUsersPerSec(1) during(1 seconds)))
    .protocols(kafkaConf)

}
