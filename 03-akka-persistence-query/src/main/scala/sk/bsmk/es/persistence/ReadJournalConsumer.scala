package sk.bsmk.es.persistence

import akka.actor.ActorSystem
import akka.persistence.jdbc.query.scaladsl.JdbcReadJournal
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import sk.bsmk.customer.persistence.TaggingEventAdapter

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ReadJournalConsumer {

  def apply(actorSystem: ActorSystem)(implicit materializer: Materializer): ReadJournalConsumer =
    new ReadJournalConsumer(actorSystem)

}

class ReadJournalConsumer(actorSystem: ActorSystem)(implicit val materializer: Materializer) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val readJournal: JdbcReadJournal =
    PersistenceQuery(actorSystem).readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)

  readJournal
    .persistenceIds()
    .runForeach(id ⇒ log.info("Persistence id = {} from ReadJournal", id))

  readJournal
    .eventsByPersistenceId("customer-1", 0, 10)
    .runForeach(envelope ⇒ log.info("Received {} from ReadJournal for customer-1", envelope))

  readJournal
    .eventsByTag(TaggingEventAdapter.CustomerAccountTag, Offset.noOffset)
    .runForeach(envelope ⇒ log.info("Received {} from ReadJournal", envelope))
//    .mapAsync(1) { envelope ⇒
//      Future {
//        log.info("Received {}", envelope)
//      }
//    }
//    .runWith(Sink.ignore)

}
