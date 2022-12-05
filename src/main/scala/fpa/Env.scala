package fpa

import pureconfig.*

sealed trait Env:

  def config[A : ConfigReader]: ConfigReader.Result[A] =
    ConfigSource.default.load[A]

object Prd extends Env

object Loc extends Env