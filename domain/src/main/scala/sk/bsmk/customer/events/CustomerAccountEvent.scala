package sk.bsmk.customer.events

import java.time.LocalDateTime

sealed trait CustomerAccountEvent

final case class CustomerAccountCreated(createdAt: LocalDateTime)            extends CustomerAccountEvent
final case class PointsAdded(pointsAdded: Int, actualPointBalance: Int)      extends CustomerAccountEvent
final case class VoucherBought(actualPointBalance: Int, voucherCode: String) extends CustomerAccountEvent
final case class VoucherSpent(voucherCode: String)                           extends CustomerAccountEvent
