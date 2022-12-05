package fpa

import scala.concurrent.*
import cats.effect._
import doobie.hikari.*
import org.flywaydb.core.*

object Database:

  def transactor(config: DatabaseConfig)(implicit ec: ExecutionContext): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](config.driver, config.url, config.user, config.password, ec)

  def initialize(transactor: HikariTransactor[IO]): IO[Unit] =
    transactor
      .configure(datasource =>
        IO.delay(
          Flyway
            .configure()
            .dataSource(datasource)
            .load()
            .migrate()))

