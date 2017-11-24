package sk.bsmk.es.persistence

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import org.jooq.DSLContext
import org.scalatest.{DoNotDiscover, Matchers, WordSpec}
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher, CreateAccount}
import sk.bsmk.customer.vouchers.{Voucher, VoucherRegistry}
import sk.bsmk.es.persistence.CustomerAccountPersistenceActor.{GetState, StoreSnapshot}
import sk.bsmk.es.persistence.model.Tables.{CUSTOMER_ACCOUNTS, VOUCHERS}

import scala.concurrent.Await
import scala.concurrent.duration._

@DoNotDiscover
class CustomerAccountPersistenceActorSpec extends WordSpec with Matchers {

  val voucher = Voucher("voucher-a", 10, 123.12)
  VoucherRegistry.add(voucher)

  val dsl: DSLContext = JooqCustomerRepository.dsl

  dsl.execute("DELETE FROM PUBLIC.\"snapshot\"")
  dsl.execute("DELETE FROM PUBLIC.\"journal\"")
  dsl.deleteFrom(VOUCHERS).execute()
  dsl.deleteFrom(CUSTOMER_ACCOUNTS).execute()

  "Customer account persistent actor" when {
    "commands are consumed" should {
      "change state" in {

        implicit val actorSystem: ActorSystem   = ActorSystem("es-system")
        implicit val materializer: Materializer = ActorMaterializer()
        val account                             = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-1"), "customer-1")

        val consumer = ReadJournalConsumer(actorSystem)

        implicit val timeout: Timeout = Timeout(5.seconds)
        def printState(): Unit = {
          val state = Await.result(account ? GetState, timeout.duration)
          pprint.pprintln(state)
        }

        account ! CreateAccount
        printState()
        account ! AddPoints(100)
        printState()
        account ! BuyVoucher(voucher.code)
        printState()
        account ! StoreSnapshot
        printState()
        Thread.sleep(10000)

        printState()

      }
    }
  }
}
