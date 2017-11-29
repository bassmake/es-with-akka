package sk.bsmk.es.persistence

import javax.sql.DataSource

import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.{BeforeAndAfter, DoNotDiscover, Matchers, WordSpec}
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher, CreateAccount}
import sk.bsmk.es.persistence.CustomerAccountPersistenceActor.{GetState, StoreSnapshot}
import akka.pattern.ask
import akka.util.Timeout
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.impl.DSL
import org.jooq.{DSLContext, SQLDialect}
import sk.bsmk.customer.vouchers.{Voucher, VoucherRegistry}

import scala.concurrent.duration._
import scala.concurrent.Await

@DoNotDiscover
class CustomerAccountPersistenceActorWithoutQuerySpec extends WordSpec with Matchers with BeforeAndAfter {

  val voucher = Voucher("voucher-a", 10, 123.12)
  VoucherRegistry.add(voucher)

  before {
    val datasource: DataSource = {
      val hikariConfig = new HikariConfig()
      hikariConfig.setJdbcUrl("jdbc:h2:file:./build/es-db")
      hikariConfig.setUsername("sa")
      new HikariDataSource(hikariConfig)
    }

    val dsl: DSLContext = DSL.using(datasource, SQLDialect.H2)
    dsl.execute("DELETE FROM PUBLIC.\"snapshot\"")
    dsl.execute("DELETE FROM PUBLIC.\"journal\"")
  }

  "Customer account persistent actor" when {
    "commands are consumed" should {
      "change state" in {
        val actorSystem = ActorSystem("es-system")

        implicit val timeout: Timeout = Timeout(60.seconds)
        def printState(actorRef: ActorRef): Unit = {
          val state = Await.result(actorRef ? GetState, timeout.duration)
          pprint.pprintln(state)
        }

        {
          val account = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-1"), "customer-1")

          account ! CreateAccount
          printState(account)
          account ! AddPoints(100)
          printState(account)
          account ! BuyVoucher(voucher.code)
          printState(account)
          account ! StoreSnapshot
          Thread.sleep(100)
          printState(account)
          actorSystem.stop(account)
        }
        pprint.pprintln("-------------")

        val account = actorSystem.actorOf(CustomerAccountPersistenceActor.props("customer-1"), "customer-1-new")
        printState(account)
      }
    }
  }
}
