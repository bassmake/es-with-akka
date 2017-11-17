package sk.bsmk.es.actors

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class CustomerAccountSpec extends WordSpec with Matchers {

  private val log = LoggerFactory.getLogger(this.getClass)

  "Customer account" when {

    "hello message is sent" should {

      "log hello" in {

        val actorSystem = ActorSystem("01")

        val actor = actorSystem.actorOf(CustomerAccount.props, "customer-1")

        actor ! "hello"

        implicit val timeout: Timeout = Timeout(5.seconds)

        val responseAny: Future[Any] = actor ? "respond"

        val response: Future[String] = responseAny.map(_.asInstanceOf[String])

        val value = Await.result(response, timeout.duration)
        log.info("Received {}", value)
      }
    }
  }

}
