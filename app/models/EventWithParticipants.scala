package models

import java.time.LocalDateTime
import java.util.UUID

case class EventWithParticipants(id: Long, title: String, description: Option[String], dateTime: LocalDateTime,
                                 maxParticipants: Option[Int], ownerId: UUID, ownerUsername: String, ownerFullName: Option[String],
                                 disciplineId: Long, disciplineNameKey: String, presenceReported: Boolean, lat: BigDecimal,
                                 lng: BigDecimal, participants: Seq[Participant]) {

  private val participantsIds = participants.map(_.userId)

  val participantsCount = participants.size

  def isOwnerOrParticipant(user: User): Boolean = {
    isOwner(user) || isParticipant(user)
  }

  def isOwner(user: User): Boolean = {
    ownerId == user.userID
  }

  def isParticipant(user: User): Boolean = {
    participantsIds.contains(user.userID)
  }

  def isNotFull: Boolean = {
    maxParticipants.map { maxParticipantsVal =>
      maxParticipantsVal > participants.size
    }.getOrElse(true)
  }

  def started: Boolean = LocalDateTime.now.isAfter(dateTime)

}
