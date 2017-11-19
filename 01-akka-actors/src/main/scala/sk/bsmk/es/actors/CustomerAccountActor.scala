package sk.bsmk.es.actors

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, Props}
import sk.bsmk.customer.CustomerAccount
import sk.bsmk.customer.commands.{AddPoints, BuyVoucher, SpendVoucher}
import sk.bsmk.customer.vouchers.VoucherRegistry
import sk.bsmk.es.actors.CustomerAccountActor.{LogState, SendState}

object CustomerAccountActor {

  def props(username: String): Props = Props(new CustomerAccountActor(username: String))

  final object LogState
  final object SendState

}

class CustomerAccountActor(val username: String) extends Actor with ActorLogging {

  var customerAccount = CustomerAccount(username, LocalDateTime.now())

  override def receive: PartialFunction[Any, Unit] = {

    case AddPoints(points) ⇒
      customerAccount = customerAccount.addPoints(points)

    case BuyVoucher(code) ⇒
      VoucherRegistry.get(code) match {
        case None          ⇒ log.error("No voucher with code '{}' found", code)
        case Some(voucher) ⇒ customerAccount = customerAccount.buyVoucher(voucher)
      }

    case SpendVoucher(code) ⇒
      VoucherRegistry.get(code) match {
        case None          ⇒ log.error("No voucher with code '{}' found", code)
        case Some(voucher) ⇒ customerAccount = customerAccount.spendVoucher(voucher)
      }

    case LogState ⇒
      log.info("Current status: {}", customerAccount)
      pprint.pprintln(customerAccount)

    case SendState ⇒
      sender() ! customerAccount
  }

}
