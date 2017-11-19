package sk.bsmk.es.persistence

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}
import sk.bsmk.customer.commands.{AddPoints, CreateAccount}
import sk.bsmk.es.persistence.CustomerAccountPersistenceActor.GetState
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Await

class CustomerAccountPersistenceActorSpec extends WordSpec with Matchers {

  "Customer account persistent actor" when {
    "commands are consumed" should {
      "change state" in {
        val actorSystem = ActorSystem("es-system")
        val account     = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-1"), "customer-1")

        account ! CreateAccount
        account ! AddPoints(100)

        implicit val timeout: Timeout = Timeout(5.seconds)
        val state                     = Await.result(account ? GetState, timeout.duration)
        pprint.pprintln(state)

//        val account2 = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-2"), "customer-2")

      }
    }
  }
}
