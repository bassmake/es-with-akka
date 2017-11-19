package sk.bsmk.customer.repository

import java.time.LocalDateTime

final case class CustomerAccountListItem(username: String,
                                         pointBalance: Int,
                                         createdAt: LocalDateTime,
                                         updatedAt: LocalDateTime,
                                         nrOfVouchers: Int)
