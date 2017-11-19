package sk.bsmk.customer.events

final case class PointsAdded(username: String, pointsAdded: Int, actualPointBalance: Int)
