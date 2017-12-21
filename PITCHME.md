@title[Title]
# Event Sourcing with akka-persistence

---
### Actor model
- Created by Carl Hewitt in 1973
- Inspired by physics
- Everything is an actor
- Erlang/Elixir
- Akka

+++
### Actors
Actor is computational entity with behavior/state that based on received message can
- send a finite number of messages to other actors |
- create a finite number of new actors |
- designate the behavior/state to be used for the next message it receives |

---
@title[Akka]
![Akka logo](https://akka.io/resources/images/akka_full_color.svg)

* Akka is a toolkit for building highly concurrent, distributed, and resilient message-driven applications for Java and Scala

+++
### Implementation
```scala
class SimpleActor extends Actor {

  var state = Set.empty[Int]

  override def receive = {
    case "add-one"   ⇒ state = state + 1
    case "add-two"   ⇒ state = state + 2
    case "get-state" ⇒ sender() ! state
  }

}
```
@[1,11](extend `Actor` trait)
@[3](internal state)
@[5-9](override receieve method)
@[6](adds 1 to state)
@[7](adds 2 to state)
@[8](sends state to sender (ask pattern))

+++
### Instantiation and usage
```scala
val system = ActorSystem("name")
val actor  = system.actorOf(Props[SimpleActor])

actor ! "add-one"
actor ! "add-two"
implicit val timeout    = Timeout(5.seconds)
val future: Future[Any] = actor ? "get-state"
```
@[1](create ActorSystem)
@[2](create actor, props is like template)
@[4](`tell` to add one, returns Unit)
@[5](`tell` to add two, returns Unit)
@[6-7](`ask` for state, needs timeout)

---
### Coding part 1

---
### CQRS
- Command Query Responsibility Segregation
- Different model is used for update and for read
- Better performance as read models can be optimized independently
- Harder to keep data consistent
- Adds complexity

---
### Event sourcing
- Every change is stored as an event
- Events are consumed asynchronously
- Easy to create new read models
- Data for audit out of the box
- Projections needed
- Higher complexity

---
### Akka persistence
- primary to persist actor's state
- only changes are persisted
- full state can be persisted via snapshot
- state is recovered by last snapshot and replaying changes
- each applied change is event 

+++
### Persistent actor
```scala
class SimplePersistenceActor extends PersistentActor {
  var state                  = Set.empty[Int]
  override def persistenceId = "some-persistence-id"
  override def receiveCommand = {
    case "add-one" ⇒  persist(1) { storedEvent ⇒
        state = state + storedEvent
    }
    case "save-snapshot" ⇒ saveSnapshot(state)
  }
  override def receiveRecover = {
    case event: Int ⇒ state = state + event
    case SnapshotOffer(_, snapshot: Set[Int]) ⇒ state = snapshot
  }
}
```
@[1,14](extend `PersistentActor` trait)
@[2](internal state)
@[3](persistenceId - must be unique for all entities)
@[4-9](receiving commands that will change state)
@[5-7](persist event and change state afterwards)
@[8](persist event snapshot)
@[10-13](recovering at creation)
@[11](replaying event)
@[12](replaying snapshot)

---
### Coding part 2

---
### Projections
- used for `read` side in CQRS
- stream of events is consumed to update projection
- different projections on the same events can be used for different cases

---
### Akka persistence query
- complements akka-persistence by providing stream based query

+++
### Read journal
```scala
val readJournal: JdbcReadJournal = PersistenceQuery(actorSystem).
  readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)
  
readJournal
  .persistenceIds()
  .runForeach(id ⇒ log.info("new PersistenceId = {}", id))
```
@[1,2](instantiate journal)
@[4-6](get akka-stream of new persistence ids)

---
### Coding part 3

---
### About Serialization in akka
- akka is not handling serialization directly
- do not use java serialization
- several libraries exists
- protobuf from Google* |
- kryo/chill from Twitter |
- avro from Apache |

+++
### Event adapters
```scala
class SimpleEventAdapter extends EventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event match {
    case _: String ⇒ Tagged(event, Set("tag"))
    case _         ⇒ event
  }

  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case Tagged(payload, tags) ⇒ EventSeq.single(event + "=" + tags)
    case _ ⇒ EventSeq.single(event)
  }
}
```
@[1,13](extend WriteEventAdapter)
@[2](useful for versioning)
@[4-7](how should be event persisted/serialization)
@[9-12](how to map from persisted form/deserialization)

---
### Thank you
useful links:
- https://akka.io/
- https://en.wikipedia.org/wiki/Actor_model
- https://martinfowler.com/bliki/CQRS.html
- https://martinfowler.com/eaaDev/EventSourcing.html

