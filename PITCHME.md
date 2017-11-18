@title[Intro]
# Event Sourcing with akka-persistence

---
@title[Akka]
![Akka logo](https://akka.io/resources/images/akka_full_color.svg)

* Akka is a toolkit for building highly concurrent, distributed, and resilient message-driven applications for Java and Scala

---
@title[Actor-model]
### Actor model
- Created by Carl Hewitt in 1973
- Inspired by physics
- Everything is an actor

+++
@title[Actors]
### Actors
Actor is computational entity with state that based on received message can
- send a finite number of messages to other actors |
- create a finite number of new actors |
- designate the behavior (state) to be used for the next message it receives |

---
@title[Akka-actors]
### Akka actors
```scala
class SimpleActor extends Actor {

  var state = Set[Int]

  override def receive = {
    case "add-one" ⇒ state = state + 1
    case "add-two" ⇒ state = state + 2
    case "get-state" ⇒ sender() ! state
  }

}
```

@[1,11](Extend actor trait)
@[5-9](Override receieve method)
@[3](Some internal state)
@[6](adds 1 to state)
@[7](adds 2 to state)
@[8](sends state to sender (ask pattern))

+++
@title[Creation and usage]
### Creation and usage
```scala
val system = ActorSystem("name")
val actor = system.actorOf(Props[SimpleActor])

actor ! "add-one"
actor ! "add-two"
implicit val timeout = Timeout(5.seconds)
val future: Future[Any] = actor ? "get-state" 
```

@[1](create ActorSystem)
@[5-9](create actor, props is like template)
@[4](`tell` to add one, returns Unit)
@[5](`tell` to add two, returns Unit)
@[5-6](`ask` for sate, needs timeout)

---
@title[CQRS]
### CQRS

---
@title[Event Sourcing]
### Event Sourcing

---
@title[Akka-persistence]
### Akka persistence

---
@title[Projections]
### Projections

---
@title[Akka-persistence-query]
### Akka persistence query

---
@title[Akka-serialization]
### About Akka serialization

---
@title[Thanks]
### Thank you
useful links:
* https://akka.io/
* https://en.wikipedia.org/wiki/Actor_model
