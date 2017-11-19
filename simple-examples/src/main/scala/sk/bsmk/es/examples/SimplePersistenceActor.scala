package sk.bsmk.es.examples

import akka.persistence.{PersistentActor, SnapshotOffer}

class SimplePersistenceActor extends PersistentActor {
  var state                  = Set.empty[Int]
  override def persistenceId = "some-persistence-id"
  override def receiveCommand = {
    case "add-one" ⇒
      val eventToStore = 1
      persist(eventToStore) { storedEvent ⇒
        state = state + storedEvent
      }
  }
  override def receiveRecover = {
    case event: Int                           ⇒ state = state + event
    case SnapshotOffer(_, snapshot: Set[Int]) ⇒ state = snapshot
  }
}
