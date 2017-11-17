package sk.bsmk.es

import org.scalatest.{BeforeAndAfter, FeatureSpec, Matchers}
import sk.bsmk.es.db.DbSupport

class EsFeatureSpec extends FeatureSpec with Matchers with DbSupport with BeforeAndAfter  {

  before {
    dsl.execute("DELETE FROM PUBLIC.\"snapshot\"")
    dsl.execute("DELETE FROM PUBLIC.\"journal\"")
  }

}
