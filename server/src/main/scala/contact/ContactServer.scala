package contact

import cats.effect.*
import fpa.*
import fs2.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.typelevel.doobie.hikari.HikariTransactor
import org.typelevel.doobie.util.*


object ContactServer extends IOApp:

  def create: IO[ExitCode] =
    resources.use(instantiate)

  private def resources: Resource[IO, Resources] =
    for
      config     <- Config.load
      ec         <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database)(using ec)
    yield
      Resources(transactor, config)

  private def instantiate(resources: Resources): IO[ExitCode] =
    for
      _          <- Database.initialize(resources.transactor)
      repository =  ContactRepository(resources.transactor)
      exitCode   <- EmberServerBuilder
                      .default[IO]
                      .withHost(resources.config.server.host)
                      .withPort(resources.config.server.port)
                      .withHttpApp(ContactService(repository).http.orNotFound)
                      .build
                      .use(_ => IO.never)
                      .as(ExitCode.Success)
    yield
      exitCode

  private case class Resources(transactor: HikariTransactor[IO], config: Config)

  def run(args: List[String]): IO[ExitCode] =
    create
