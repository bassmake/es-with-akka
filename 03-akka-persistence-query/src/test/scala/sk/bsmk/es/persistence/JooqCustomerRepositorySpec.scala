package sk.bsmk.es.persistence

import org.scalatest.{Matchers, WordSpec}
import sk.bsmk.customer.vouchers.Voucher
import sk.bsmk.es.persistence.model.Tables.{CUSTOMER_ACCOUNTS, VOUCHERS}

class JooqCustomerRepositorySpec extends WordSpec with Matchers {

  "Customer repository" when {

    val repository = JooqCustomerRepository
    val dsl        = repository.dsl

    "some data are stored" should {

      dsl.deleteFrom(VOUCHERS).execute()
      dsl.deleteFrom(CUSTOMER_ACCOUNTS).execute()

      prepareCustomer1()
      prepareCustomer2()
      prepareCustomer3()
      prepareCustomer4()

      "return customer accounts" in {
        val list = repository.listCustomerAccounts()
        list should have size 4
        pprint.pprintln(list)
      }

      "return customer accounts with voucher" in {
        val list = repository.listCustomerAccountsWithVouchers()
        list should have size 6
        pprint.pprintln(list)
      }
    }
    def prepareCustomer1(): Unit = {
      val username = "customer-1"
      repository.insertCustomerAccount(username)
      repository.updatePoints(username, 100)
      repository.insertVoucherAndUpdatePoints(username, 90, Voucher("voucher-1-a", 10, 100))
    }
    def prepareCustomer2(): Unit = {
      val username = "customer-2"
      repository.insertCustomerAccount(username)
      repository.updatePoints(username, 200)
      repository.insertVoucherAndUpdatePoints(username, 170, Voucher("voucher-2-a", 30, 130))
      repository.insertVoucherAndUpdatePoints(username, 130, Voucher("voucher-2-b", 40, 130))
      repository.insertVoucherAndUpdatePoints(username, 80, Voucher("voucher-2-c", 50, 130))
    }
    def prepareCustomer3(): Unit = {
      val username = "customer-3"
      repository.insertCustomerAccount(username)
      repository.updatePoints(username, 300)
    }
    def prepareCustomer4(): Unit = {
      repository.insertCustomerAccount("customer-4")
    }
  }

}
