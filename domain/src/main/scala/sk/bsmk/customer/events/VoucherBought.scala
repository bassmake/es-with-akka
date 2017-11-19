package sk.bsmk.customer.events

final case class VoucherBought(username: String, actualPointBalance: Int, voucherCode: String)
