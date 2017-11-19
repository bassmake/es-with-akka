package sk.bsmk.es.persistence

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

class CustomerAccountPersistenceActor(val persistenceId: String) extends PersistentActor with ActorLogging {
  override def receiveRecover = ???

  override def receiveCommand = ???

}
