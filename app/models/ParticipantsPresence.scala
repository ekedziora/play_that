package models

import java.util.UUID

case class ParticipantsPresence(userId: UUID, username: String, fullName: Option[String], present: Boolean)
