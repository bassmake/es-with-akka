package sk.bsmk.es.examples

import akka.persistence.journal.{EventAdapter, EventSeq, Tagged}

class SimpleEventAdapter extends EventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event match {
    case _: String ⇒ Tagged(event, Set("tag"))
    case _         ⇒ event
  }

  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case Tagged(payload, tags) ⇒ EventSeq.single("mapped")
    case _                     ⇒ EventSeq.single(event)
  }
}
