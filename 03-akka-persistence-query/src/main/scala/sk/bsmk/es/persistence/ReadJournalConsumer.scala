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

class ReadJournalConsumer(actorSystem: ActorSystem)(implicit val materializer: Materializer) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val readJournal: JdbcReadJournal =
    PersistenceQuery(actorSystem).readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)

  readJournal
    .eventsByTag(TaggingEventAdapter.CustomerAccountTag, Offset.noOffset)
    .mapAsync(1) { envelope ⇒
      Future {
        log.info("Received {}", envelope)
      }
    }
    .runWith(Sink.ignore)

}
