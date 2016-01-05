package models

import java.util.UUID

case class Participant(id: Long, userId:UUID, username: String, fullName: Option[String])
