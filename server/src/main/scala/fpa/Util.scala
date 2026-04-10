package fpa

import fs2.*

object Util:

    extension [F[_], A](fa: F[A])
      def stream: Stream[F, A] = Stream.eval(fa)
