@title[Title]
# Event Sourcing with akka-persistence

---
@title[Akka]
![Akka logo](https://akka.io/resources/images/akka_full_color.svg)

* Akka is a toolkit for building highly concurrent, distributed, and resilient message-driven applications for Java and Scala

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
### Akka actors
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

@[1,11](extend actor trait)
@[5-9](override receieve method)
@[3](some internal state)
@[6](adds 1 to state)
@[7](adds 2 to state)
@[8](sends state to sender (ask pattern))

+++
### Creation and usage
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
- TODO

+++
### Persistent actor
```scala
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
```

---
### Coding part 2

---
### Projections
TODO

---
### Akka persistence query
TODO

---
### Coding part 3

---
### About Akka serialization
TODO

---
### Thank you
useful links:
- https://akka.io/
- https://en.wikipedia.org/wiki/Actor_model
- https://martinfowler.com/bliki/CQRS.html
- https://martinfowler.com/eaaDev/EventSourcing.html

