package sk.bsmk.es.persistence

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import org.jooq.DSLContext
import org.scalatest.{Ignore, Matchers, WordSpec}
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher, CreateAccount}
import sk.bsmk.customer.vouchers.{Voucher, VoucherRegistry}
import sk.bsmk.es.persistence.CustomerAccountPersistenceActor.{GetState, StoreSnapshot}
import sk.bsmk.es.persistence.model.Tables.{CUSTOMER_ACCOUNTS, VOUCHERS}

import scala.concurrent.Await
import scala.concurrent.duration._

@Ignore
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

        val actorSystem = ActorSystem("es-system")
        val account     = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-1"), "customer-1")

        implicit val timeout: Timeout = Timeout(5.seconds)
        def printState(): Unit = {
          val state = Await.result(account ? GetState, timeout.duration)
          pprint.pprintln(state)
        }

        account ! CreateAccount
        account ! AddPoints(100)

        printState()

        account ! BuyVoucher(voucher.code)

        account ! StoreSnapshot

        printState()

      }
    }
  }
}
