package sk.bsmk.es.persistence

import akka.actor.ActorSystem
import akka.persistence.jdbc.query.scaladsl.JdbcReadJournal
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import sk.bsmk.customer.events.{CustomerAccountCreated, PointsAdded, VoucherBought, VoucherSpent}
import sk.bsmk.customer.persistence.TaggingEventAdapter
import sk.bsmk.customer.vouchers.VoucherRegistry

import scala.concurrent.Future

object ReadJournalConsumer {

  def apply(actorSystem: ActorSystem)(implicit materializer: Materializer): ReadJournalConsumer =
    new ReadJournalConsumer(actorSystem)

}

class ReadJournalConsumer(actorSystem: ActorSystem)(implicit val materializer: Materializer) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val readJournal: JdbcReadJournal =
    PersistenceQuery(actorSystem).readJournalFor[JdbcReadJournal](JdbcReadJournal.Identifier)

//  readJournal
//    .persistenceIds()
//    .runForeach(id ⇒ log.info("Persistence id = {} from ReadJournal", id))

//  readJournal
//    .eventsByPersistenceId("customer-1", 0, 10)
//    .runForeach(envelope ⇒ log.info("Received {} from ReadJournal for customer-1", envelope))

  readJournal
    .currentEventsByTag(TaggingEventAdapter.CustomerAccountTag, Offset.noOffset)
    .map(envelope ⇒ {
      log.info("Received {} from ReadJournal", envelope)
      envelope
    })
    .mapAsync(1) { envelope ⇒
      val persistenceId = envelope.persistenceId
      val event         = envelope.event
      event match {
        case CustomerAccountCreated(createdAt) ⇒ JooqCustomerRepository.insertCustomerAccount(persistenceId, createdAt)
        case PointsAdded(pointsAdded)          ⇒ JooqCustomerRepository.updatePoints(persistenceId, pointsAdded)
        case VoucherBought(voucherCode) ⇒
          VoucherRegistry.get(voucherCode) match {
            case Some(voucher) ⇒
              JooqCustomerRepository.insertVoucherAndUpdatePoints(persistenceId, voucher.points, voucher)
            case None ⇒
              log.error("Voucher {} not found", voucherCode)
              Future.successful("")
          }
        case VoucherSpent(voucherCode) ⇒ JooqCustomerRepository.deleteVoucher(persistenceId, voucherCode)
      }
    }
    .runWith(Sink.ignore)

}
