package sk.bsmk.es.customer

import java.time.{LocalDate, LocalDateTime}
import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.{DSLContext, SQLDialect}
import org.jooq.impl.DSL
import sk.bsmk.customer.vouchers.Voucher
import sk.bsmk.es.persistence.model.Tables._

object CustomerRepository {

  val datasource: DataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl("jdbc:h2:file:./build/customer")
    hikariConfig.setUsername("sa")
    new HikariDataSource(hikariConfig)
  }

  val dsl: DSLContext = DSL.using(datasource, SQLDialect.H2)

  def insertCustomerAccount(username: String): Unit = {
    dsl
      .insertInto(CUSTOMER_ACCOUNTS)
      .set(CUSTOMER_ACCOUNTS.USERNAME, username)
      .set(CUSTOMER_ACCOUNTS.POINT_BALANCE, int2Integer(0))
      .set(CUSTOMER_ACCOUNTS.CREATED_AT, LocalDateTime.now())
      .set(CUSTOMER_ACCOUNTS.UPDATED_AT, LocalDateTime.now())
      .execute()
  }

  def updatePoints(username: String, newPointBalance: Int): Unit = {
    dsl
      .update(CUSTOMER_ACCOUNTS)
      .set(CUSTOMER_ACCOUNTS.POINT_BALANCE, int2Integer(newPointBalance))
      .set(CUSTOMER_ACCOUNTS.UPDATED_AT, LocalDateTime.now())
      .where(CUSTOMER_ACCOUNTS.USERNAME.eq(username))
      .execute()
  }

  def insertVoucherAndUpdatePoints(username: String, newPointBalance: Int, voucher: Voucher): Unit = {
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

}
