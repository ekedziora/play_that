package models

import java.time.LocalDateTime
import java.util.UUID

case class Event(id: Long, title: String, description: Option[String], dateTime: LocalDateTime, maxParticipants: Option[Int],
                 ownerId: UUID, ownerUsername: String, ownerFullName: Option[String], disciplineId: Long,
                 disciplineNameKey: String, disciplinePhotoName: Option[String])