package sk.bsmk.customer.repository

import sk.bsmk.customer.vouchers.Voucher

import scala.concurrent.Future

trait CustomerRepository {

  def insertCustomerAccount(username: String): Future[Unit]
  def updatePoints(username: String, newPointBalance: Int): Future[Unit]
  def insertVoucherAndUpdatePoints(username: String, newPointBalance: Int, voucher: Voucher): Future[Unit]
  def deleteVoucher(username: String, voucherCode: String): Future[Unit]

  def listCustomerAccounts(): List[CustomerAccountListItem]
  def listCustomerAccountsWithVouchers(): List[CustomerAccountVoucherListItem]

}
