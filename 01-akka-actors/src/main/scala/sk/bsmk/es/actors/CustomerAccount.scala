package sk.bsmk.es.actors

import akka.actor.{Actor, ActorLogging, Props}

object CustomerAccount {

  def props: Props = Props[CustomerAccount]

}

class CustomerAccount extends Actor with ActorLogging {

  override def receive: PartialFunction[Any, Unit] = {

    case m @ "hello" ⇒ log.info("I received {} message", m)

    case m @ "respond" ⇒
      log.info("I received {} message", m)
      sender() ! "responding"

  }

}
