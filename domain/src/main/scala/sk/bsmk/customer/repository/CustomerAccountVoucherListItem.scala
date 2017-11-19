package sk.bsmk.customer.repository

import java.time.LocalDateTime

final case class CustomerAccountVoucherListItem(username: String,
                                                pointBalance: Int,
                                                createdAt: LocalDateTime,
                                                updatedAt: LocalDateTime,
                                                voucherCode: String,
                                                voucherValue: Double)
