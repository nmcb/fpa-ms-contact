package fpa

import fs2.*

object Util {

    implicit class EffectOps[F[_], A](fa: F[A]) {
      def stream: Stream[F, A] = Stream.eval(fa)
    }
}
