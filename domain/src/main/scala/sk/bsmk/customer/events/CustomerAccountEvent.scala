package sk.bsmk.customer.events

import java.time.LocalDateTime

sealed trait CustomerAccountEvent

final case class CustomerAccountCreated(createdAt: LocalDateTime) extends CustomerAccountEvent
final case class PointsAdded(pointsAdded: Int)                    extends CustomerAccountEvent
final case class VoucherBought(voucherCode: String)               extends CustomerAccountEvent
final case class VoucherSpent(voucherCode: String)                extends CustomerAccountEvent
