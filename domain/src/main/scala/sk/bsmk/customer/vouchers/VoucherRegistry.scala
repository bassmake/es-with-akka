package sk.bsmk.customer.vouchers

import scala.collection.mutable

object VoucherRegistry {

  private val vouchers = mutable.HashMap.empty[String, Voucher]

  def add(voucher: Voucher): Option[Voucher] = {
    vouchers.put(voucher.code, voucher)
  }

  def get(code: String): Option[Voucher] = {
    vouchers.get(code)
  }

}
