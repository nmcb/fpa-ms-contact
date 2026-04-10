package fpa

import fs2.*

object stream {

    implicit class EffectOps[F[_], A](fa: F[A]) {
      def stream: Stream[F, A] = Stream.eval(fa)
    }
}
