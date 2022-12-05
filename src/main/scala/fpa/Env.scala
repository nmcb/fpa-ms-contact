package fpa

import pureconfig.*
import pureconfig.error.*

sealed trait Env:

  import scala.reflect.ClassTag

  def config[A : ConfigReader : ClassTag]: ConfigReader.Result[A] =
    ConfigSource.default.load[A]

object Prd extends Env

object Loc extends Env