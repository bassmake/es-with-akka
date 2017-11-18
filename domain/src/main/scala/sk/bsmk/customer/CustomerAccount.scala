package sk.bsmk.customer

final case class CustomerAccount(username: String, pointBalance: Int = 0, vouchers: Set[Voucher] = Set.empty) {

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