package sk.bsmk.customer.repository

import sk.bsmk.customer.vouchers.Voucher

trait CustomerRepository {

  def insertCustomerAccount(username: String): Unit
  def updatePoints(username: String, newPointBalance: Int): Unit
  def insertVoucherAndUpdatePoints(username: String, newPointBalance: Int, voucher: Voucher): Unit
  def deleteVoucher(username: String, voucherCode: String): Unit

  def listCustomerAccounts(): List[CustomerAccountListItem]
  def listCustomerAccountsWithVouchers(): List[CustomerAccountVoucherListItem]

}
