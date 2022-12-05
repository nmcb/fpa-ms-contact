package contact

import cats.effect.*
import cats.implicits.*
import fs2.*

import org.http4s.implicits.*
import org.http4s.blaze.server.*
import org.http4s.dsl.io.*

import doobie.util.*
import doobie.hikari.*

import fpa.*

import pureconfig.error.*


object ContactServer extends IOApp:

  def instantiate: IO[ExitCode] =
    resources.use(create)

  def config: Resource[IO, Config] =
    val config = IO.delay(Prd.config[Config]).flatMap {
      case Left(error)  => IO.raiseError[Config](new ConfigReaderException[Config](error))
      case Right(value) => IO.pure(value)
    }
    Resource.eval(config)

  def resources: Resource[IO, Resources] =
    for {
      config     <- config
      ec         <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database)(ec)
    } yield Resources(transactor, config)

  def create(resources: Resources): IO[ExitCode] =
    for {
      _          <- Database.initialize(resources.transactor)
      repository =  ContactRepository(resources.transactor)
      exitCode   <- BlazeServerBuilder[IO]
                      .bindHttp(resources.config.server.port, resources.config.server.host)
                      .withHttpApp(ContactService(repository).http.orNotFound)
                      .serve
                      .compile
                      .lastOrError
    } yield exitCode

  case class Resources(transactor: HikariTransactor[IO], config: Config)

  def run(args: List[String]): IO[ExitCode] =
    instantiate

