package sk.bsmk.es.examples

import akka.persistence.{PersistentActor, SnapshotOffer}

class SimplePersistenceActor extends PersistentActor {
  var state                  = Set.empty[Int]
  override def persistenceId = "some-persistence-id"
  override def receiveCommand = {
    case "add-one" ⇒
      persist(1) { storedEvent ⇒
        state = state + storedEvent
      }
    case "save-snapshot" ⇒ saveSnapshot(state)
  }
  override def receiveRecover = {
    case event: Int                           ⇒ state = state + event
    case SnapshotOffer(_, snapshot: Set[Int]) ⇒ state = snapshot
  }
}
