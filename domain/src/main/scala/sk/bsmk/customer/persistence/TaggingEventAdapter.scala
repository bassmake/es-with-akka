package sk.bsmk.customer.persistence

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import sk.bsmk.customer.events.CustomerAccountEvent

object TaggingEventAdapter {
  val CustomerAccountTag = "CustomerAccount"
}

class TaggingEventAdapter extends WriteEventAdapter {

  override def manifest(event: Any) = ""

  override def toJournal(event: Any): Any = event match {
    case _: CustomerAccountEvent ⇒ Tagged(event, Set(TaggingEventAdapter.CustomerAccountTag))
    case _                       ⇒ event
  }
}
