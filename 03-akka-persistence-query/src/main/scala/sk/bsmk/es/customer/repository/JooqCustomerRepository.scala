package sk.bsmk.es.customer.repository

import java.time.LocalDateTime
import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.impl.DSL
import org.jooq.impl.DSL._
import org.jooq._
import sk.bsmk.customer.repository.{CustomerAccountListItem, CustomerAccountVoucherListItem, CustomerRepository}
import sk.bsmk.customer.vouchers.Voucher
import sk.bsmk.es.persistence.model.Tables._

import scala.collection.JavaConverters._

object JooqCustomerRepository extends CustomerRepository {

  private type CustomerAccountListItemRecord = Record5[String, Integer, LocalDateTime, LocalDateTime, Integer]
  private type CustomerAccountVoucherListItemRecord =
    Record6[String, Integer, LocalDateTime, LocalDateTime, String, java.lang.Double]

  private val CustomerAccountListItemMapper = new RecordMapper[CustomerAccountListItemRecord, CustomerAccountListItem] {
    override def map(record: CustomerAccountListItemRecord): CustomerAccountListItem = CustomerAccountListItem(
      username = record.component1(),
      pointBalance = record.component2(),
      createdAt = record.component3(),
      updatedAt = record.component4(),
      nrOfVouchers = record.component5()
    )
  }

  private val CustomerAccountVoucherListItemMapper =
    new RecordMapper[CustomerAccountVoucherListItemRecord, CustomerAccountVoucherListItem] {
      override def map(record: CustomerAccountVoucherListItemRecord): CustomerAccountVoucherListItem =
        CustomerAccountVoucherListItem(
          username = record.component1(),
          pointBalance = record.component2(),
          createdAt = record.component3(),
          updatedAt = record.component4(),
          voucherCode = record.component5(),
          voucherValue = record.component6()
        )
    }

  val datasource: DataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl("jdbc:h2:file:./03-akka-persistence-query/build/es-db")
    hikariConfig.setUsername("sa")
    new HikariDataSource(hikariConfig)
  }

  val dsl: DSLContext = DSL.using(datasource, SQLDialect.H2)

  override def insertCustomerAccount(username: String): Unit = {
    dsl
      .insertInto(CUSTOMER_ACCOUNTS)
      .set(CUSTOMER_ACCOUNTS.USERNAME, username)
      .set(CUSTOMER_ACCOUNTS.POINT_BALANCE, int2Integer(0))
      .set(CUSTOMER_ACCOUNTS.CREATED_AT, LocalDateTime.now())
      .set(CUSTOMER_ACCOUNTS.UPDATED_AT, LocalDateTime.now())
      .execute()
  }

  override def updatePoints(username: String, newPointBalance: Int): Unit = {
    dsl
      .update(CUSTOMER_ACCOUNTS)
      .set(CUSTOMER_ACCOUNTS.POINT_BALANCE, int2Integer(newPointBalance))
      .set(CUSTOMER_ACCOUNTS.UPDATED_AT, LocalDateTime.now())
      .where(CUSTOMER_ACCOUNTS.USERNAME.eq(username))
      .execute()
  }

  override def insertVoucherAndUpdatePoints(username: String, newPointBalance: Int, voucher: Voucher): Unit = {
    dsl.transaction(ctx ⇒ {
      DSL
        .using(ctx)
        .insertInto(VOUCHERS)
        .set(VOUCHERS.CODE, voucher.code)
        .set(VOUCHERS.USERNAME, username)
        .set(VOUCHERS.VALUE, double2Double(voucher.value))
        .execute()

      DSL
        .using(ctx)
        .update(CUSTOMER_ACCOUNTS)
        .set(CUSTOMER_ACCOUNTS.POINT_BALANCE, int2Integer(newPointBalance))
        .set(CUSTOMER_ACCOUNTS.UPDATED_AT, LocalDateTime.now())
        .where(CUSTOMER_ACCOUNTS.USERNAME.eq(username))
        .execute()

    })
  }

  override def listCustomerAccounts(): List[CustomerAccountListItem] = {
    dsl
      .select(
        CUSTOMER_ACCOUNTS.USERNAME,
        CUSTOMER_ACCOUNTS.POINT_BALANCE,
        CUSTOMER_ACCOUNTS.CREATED_AT,
        CUSTOMER_ACCOUNTS.UPDATED_AT,
        count(VOUCHERS)
      )
      .from(CUSTOMER_ACCOUNTS)
      .leftOuterJoin(VOUCHERS)
      .onKey()
      .groupBy(
        CUSTOMER_ACCOUNTS.USERNAME,
        CUSTOMER_ACCOUNTS.POINT_BALANCE,
        CUSTOMER_ACCOUNTS.CREATED_AT,
        CUSTOMER_ACCOUNTS.UPDATED_AT
      )
      .orderBy(CUSTOMER_ACCOUNTS.USERNAME)
      .fetch(CustomerAccountListItemMapper)
      .asScala
      .toList
  }

  override def listCustomerAccountsWithVouchers(): List[CustomerAccountVoucherListItem] = {
    dsl
      .select(
        CUSTOMER_ACCOUNTS.USERNAME,
        CUSTOMER_ACCOUNTS.POINT_BALANCE,
        CUSTOMER_ACCOUNTS.CREATED_AT,
        CUSTOMER_ACCOUNTS.UPDATED_AT,
        VOUCHERS.CODE,
        VOUCHERS.VALUE
      )
      .from(CUSTOMER_ACCOUNTS)
      .leftOuterJoin(VOUCHERS)
      .onKey()
      .orderBy(CUSTOMER_ACCOUNTS.USERNAME)
      .fetch(CustomerAccountVoucherListItemMapper)
      .asScala
      .toList
  }

}
