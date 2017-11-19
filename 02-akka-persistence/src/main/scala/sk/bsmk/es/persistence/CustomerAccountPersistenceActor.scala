package sk.bsmk.es.persistence

import java.time.LocalDateTime

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import sk.bsmk.customer.CustomerAccount
import sk.bsmk.customer.events._
import sk.bsmk.customer.vouchers.VoucherRegistry

object CustomerAccountPersistenceActor {

  def props(username: String): Props = Props(new CustomerAccountPersistenceActor(username))

}

class CustomerAccountPersistenceActor(val username: String) extends PersistentActor with ActorLogging {

  override def persistenceId: String = username
  var state                          = CustomerAccount(username, LocalDateTime.now())

  private def updateState(event: CustomerAccountEvent): Unit = event match {
    case CustomerAccountCreated(createdAt) ⇒ state = CustomerAccount(username, createdAt)
    case PointsAdded(pointsAdded, _)       ⇒ state = state.addPoints(pointsAdded)
    case VoucherBought(_, voucherCode) ⇒
      VoucherRegistry.get(voucherCode) match {
        case None          ⇒ log.error("No voucher with code '{}'", voucherCode)
        case Some(voucher) ⇒ state = state.buyVoucher(voucher)
      }
    case VoucherSpent(voucherCode) ⇒
      VoucherRegistry.get(voucherCode) match {
        case None          ⇒ log.error("No voucher with code '{}'", voucherCode)
        case Some(voucher) ⇒ state = state.spendVoucher(voucher)
      }
  }

  override def receiveCommand = {
    ???
  }

  override def receiveRecover = {
    ???
  }

}
