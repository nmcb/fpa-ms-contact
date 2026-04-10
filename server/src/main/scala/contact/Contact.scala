package contact

import cats.Monad
import doobie._
import io.circe._
import io.circe.generic.semiauto._
import fpa._

enum Importance(val value: String):
  case High   extends Importance("high")
  case Medium extends Importance("medium")
  case Low    extends Importance("low")

import Importance.*

object Importance:

  def unsafeFromString(value: String): Importance =
    values.find(_.value == value).get

  given Encoder[Importance] =
    Encoder.encodeString.contramap[Importance](_.value)

  given Decoder[Importance] =
    Decoder.decodeString.map[Importance](Importance.unsafeFromString)

  given Meta[Importance] =
    Meta[String].imap(Importance.unsafeFromString)(_.value)


case class Contact(id: Option[Identity], description: String, importance: Importance)

object Contact:

  given Encoder[Contact] =
    deriveEncoder[Contact]

  given Decoder[Contact] =
    deriveDecoder[Contact]

  given [F[_] : Monad] => HasIdentity[F, Contact] =
    new HasIdentity[F, Contact]:

      def id(contact: Contact): F[Option[Identity]] =
        summon[Monad[F]].pure(contact.id)

      def withId(contact: Contact)(id: Identity): F[Contact] =
        summon[Monad[F]].pure(contact.copy(id = Some(id)))
