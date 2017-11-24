package sk.bsmk.es.persistence

import java.time.LocalDateTime

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import sk.bsmk.customer.CustomerAccount
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher, CreateAccount, SpendVoucher}
import sk.bsmk.customer.events._
import sk.bsmk.customer.vouchers.VoucherRegistry
import sk.bsmk.es.persistence.CustomerAccountPersistenceActor.{GetState, StoreSnapshot}

object CustomerAccountPersistenceActor {

  def props(username: String): Props = Props(new CustomerAccountPersistenceActor(username))

  object GetState
  object StoreSnapshot

}

class CustomerAccountPersistenceActor(val username: String) extends PersistentActor with ActorLogging {

  override def persistenceId: String = username
  var state                          = CustomerAccount(username, LocalDateTime.now())

  private def updateState(event: CustomerAccountEvent): Unit = event match {
    case CustomerAccountCreated(createdAt) ⇒ state = CustomerAccount(username, createdAt)
    case PointsAdded(pointsAdded)          ⇒ state = state.addPoints(pointsAdded)
    case VoucherBought(voucherCode) ⇒
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

  override def receiveCommand: Receive = {
    case CreateAccount ⇒
      persist(CustomerAccountCreated(LocalDateTime.now())) { event ⇒
        updateState(event)
      }
    case AddPoints(points) ⇒
      persist(PointsAdded(points)) { event ⇒
        updateState(event)
      }
    case BuyVoucher(voucherCode) ⇒
      persist(VoucherBought(voucherCode)) { event ⇒
        updateState(event)
      }
    case SpendVoucher(voucherCode) ⇒
      persist(VoucherSpent(voucherCode)) { event ⇒
        updateState(event)
      }
    case StoreSnapshot                         ⇒ saveSnapshot(state)
    case SaveSnapshotSuccess(metadata)         ⇒ log.info("Snapshot succeeded {}", metadata)
    case SaveSnapshotFailure(metadata, reason) ⇒ log.error("Snapshot failed {}", reason, metadata)
    case GetState                              ⇒ sender() ! state
  }

  override def receiveRecover: Receive = {
    case event: CustomerAccountEvent ⇒
      log.info("Processing recovery event {}", event)
      updateState(event)
    case SnapshotOffer(_, snapshot: CustomerAccount) ⇒
      log.info("Processing recovery snapshot {}", snapshot)
      state = snapshot
  }

}
