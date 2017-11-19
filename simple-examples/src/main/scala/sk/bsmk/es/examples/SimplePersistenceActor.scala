package sk.bsmk.es.examples

import akka.persistence.{PersistentActor, SnapshotOffer}

class SimplePersistenceActor extends PersistentActor {

  val snapShotInterval = 1000
  var state            = Set.empty[Int]

  override def persistenceId = "some-persistence-id"

  override def receiveCommand = {
    case "add-one" ⇒
      val eventToStore = 1
      persist(eventToStore) { storedEvent ⇒
        state = state + storedEvent
        if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
          saveSnapshot(state)
      }
  }

  override def receiveRecover = {
    case event: Int                           ⇒ state = state + event
    case SnapshotOffer(_, snapshot: Set[Int]) ⇒ state = snapshot
  }

}
