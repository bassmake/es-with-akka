package sk.bsmk.customer

import java.time.LocalDateTime

import sk.bsmk.customer.vouchers.Voucher

final case class CustomerAccount(username: String,
                                 createdAt: LocalDateTime,
                                 pointBalance: Int = 0,
                                 vouchers: Set[Voucher] = Set.empty) {

  def addPoints(points: Int): CustomerAccount = {
    this.copy(
      pointBalance = this.pointBalance + points
    )
  }

  def buyVoucher(voucher: Voucher): CustomerAccount = {
    this.copy(
      pointBalance = this.pointBalance - voucher.points,
      vouchers = this.vouchers + voucher
    )
  }

  def spendVoucher(voucher: Voucher): CustomerAccount = {
    this.copy(
      vouchers = this.vouchers - voucher
    )
  }

}
