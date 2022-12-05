package fpa

import pureconfig.*
import pureconfig.generic.derivation.default.*

case class ServerConfig(host: String, port: Int)
  derives ConfigReader

case class DatabaseConfig(driver: String, url: String, user: String, password: String, threadPoolSize: Int)
  derives ConfigReader

case class LoggingConfig(logHeaders: Boolean, logBody: Boolean)
  derives ConfigReader

case class Config(server: ServerConfig, database: DatabaseConfig, logging: LoggingConfig)
  derives ConfigReader
