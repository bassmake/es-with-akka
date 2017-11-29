package sk.bsmk.customer

import java.time.LocalDateTime

import org.slf4j.LoggerFactory
import sk.bsmk.customer.vouchers.{Voucher, VoucherRegistry}

final case class CustomerAccount(username: String,
                                 createdAt: LocalDateTime,
                                 pointBalance: Int = 0,
                                 vouchers: Set[Voucher] = Set.empty) {

  private val log = LoggerFactory.getLogger(CustomerAccount.getClass)

  def addPoints(points: Int): CustomerAccount = {
    this.copy(
      pointBalance = this.pointBalance + points
    )
  }

  def buyVoucher(voucherCode: String): CustomerAccount =
    VoucherRegistry.get(voucherCode) match {
      case None ⇒
        log.error("No voucher with code '{}'", voucherCode)
        this
      case Some(voucher) ⇒
        this.copy(
          pointBalance = this.pointBalance - voucher.points,
          vouchers = this.vouchers + voucher
        )
    }

  def spendVoucher(voucherCode: String): CustomerAccount =
    VoucherRegistry.get(voucherCode) match {
      case None ⇒
        log.error("No voucher with code '{}'", voucherCode)
        this
      case Some(voucher) ⇒
        this.copy(
          vouchers = this.vouchers - voucher
        )
    }

}
