package forms

import java.util.UUID

import play.api.data.Form
import play.api.data.Forms._

object ParticipantsPresenceForm {

  val form = Form(
    mapping(
      "participantsPresence" -> list(
        mapping(
          "userId" -> uuid,
          "present" -> boolean
        )(ParticipantPresence.apply)(ParticipantPresence.unapply)
      )
    )(Data.apply)(Data.unapply)
  )

  case class Data(participantsPresence: List[ParticipantPresence])

  case class ParticipantPresence(userId: UUID, present: Boolean)

}
