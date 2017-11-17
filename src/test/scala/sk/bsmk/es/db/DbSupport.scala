package sk.bsmk.es.db

import java.util.concurrent.Executors

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.{DSLContext, SQLDialect}
import org.jooq.impl.DSL

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

trait DbSupport {

  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl("jdbc:h2:file:./build/customer")
  hikariConfig.setUsername("sa")

  private val datasource = new HikariDataSource(hikariConfig)

  implicit val ec: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  protected val dsl: DSLContext = DSL.using(datasource, SQLDialect.H2)

}
