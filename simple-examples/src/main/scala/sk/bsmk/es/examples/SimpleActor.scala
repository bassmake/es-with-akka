package sk.bsmk.es.examples

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

class SimpleActor extends Actor {

  var state = Set.empty[Int]

  override def receive = {
    case "add-one"   ⇒ state = state + 1
    case "add-two"   ⇒ state = state + 2
    case "get-state" ⇒ sender() ! state
  }

}

object SimpleActor extends App {

  val system = ActorSystem("name")
  val actor  = system.actorOf(Props[SimpleActor])

  actor ! "add-one"
  actor ! "add-two"
  implicit val timeout    = Timeout(5.seconds)
  val future: Future[Any] = actor ? "get-state"

  system.terminate()

}
