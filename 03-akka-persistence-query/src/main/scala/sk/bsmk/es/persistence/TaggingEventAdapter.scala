package sk.bsmk.es.persistence

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import sk.bsmk.customer.events.CustomerAccountEvent

object TaggingEventAdapter {
  val CustomerAccountTag = "CustomerAccount"
}

class TaggingEventAdapter extends WriteEventAdapter {

  override def manifest(event: Any) = ""

  override def toJournal(event: Any) = event match {
    case _: CustomerAccountEvent ⇒ Tagged(event, Set(CustomerAccountTag))
    case _ ⇒ event
  }
}
