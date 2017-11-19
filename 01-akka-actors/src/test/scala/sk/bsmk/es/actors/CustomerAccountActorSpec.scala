package sk.bsmk.es.actors

import akka.actor.ActorSystem
import org.scalatest.{Matchers, WordSpec}
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory
import sk.bsmk.customer.CustomerAccount
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher}
import sk.bsmk.customer.vouchers.{Voucher, VoucherRegistry}
import sk.bsmk.es.actors.CustomerAccountActor.{LogState, SendState}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class CustomerAccountActorSpec extends WordSpec with Matchers {

  private val log = LoggerFactory.getLogger(this.getClass)

  "Customer account actor" when {

    "points are added and voucher is bought" should {

      val actorSystem = ActorSystem("01")

      val voucher = Voucher("1", 100, 5)
      VoucherRegistry.add(voucher)

      val actor = actorSystem.actorOf(CustomerAccountActor.props("alice"), "customer-1")

      actor ! AddPoints(300)
      actor ! BuyVoucher(voucher.code)

      "log correct state" in {
        actor ! LogState
      }

      "send correct state" in {
        implicit val timeout: Timeout = Timeout(5.seconds)

        val responseAny: Future[Any] = actor ? SendState

        val response: Future[CustomerAccount] = responseAny.map(_.asInstanceOf[CustomerAccount])

        val account = Await.result(response, timeout.duration)
        log.info("Received {}", account)
        pprint.pprintln(account)

        account.username shouldBe "alice"
        account.pointBalance shouldBe 200
        account.vouchers should have size 1

      }
    }
  }

}
