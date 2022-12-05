package contact

import cats.Monad
import doobie._
import io.circe._
import io.circe.generic.semiauto._
import fpa._

enum Importance(val underlying: String):
  case High   extends Importance("high")
  case Medium extends Importance("medium")
  case Low    extends Importance("low")

object Importance:

  def unsafeFromString(value: String): Importance =
    values.find(_.underlying == value).get

  implicit val importanceEncoder: Encoder[Importance] =
    Encoder.encodeString.contramap[Importance](_.underlying)

  implicit val importanceDecoder: Decoder[Importance] =
    Decoder.decodeString.map[Importance](Importance.unsafeFromString)

  implicit val importanceMeta: Meta[Importance] =
    Meta[String].imap(Importance.unsafeFromString)(_.underlying)


case class Contact(id: Option[Identity], description: String, importance: Importance)

object Contact:

  implicit val contactEncoder: Encoder[Contact] =
    deriveEncoder[Contact]

  implicit val contactDecoder: Decoder[Contact] =
    deriveDecoder[Contact]

  implicit def contactEntity[F[_]](implicit F: Monad[F]): HasIdentity[F, Contact] =
    new HasIdentity[F, Contact]:

      def id(contact: Contact): F[Option[Identity]] =
        F.pure(contact.id)

      def withId(contact: Contact)(id: Identity): F[Contact] =
        F.pure(contact.copy(id = Some(id)))
