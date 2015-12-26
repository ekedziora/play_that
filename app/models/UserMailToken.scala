package models

import java.time.LocalDateTime
import java.util.UUID

case class UserMailToken(id: UUID, userId: UUID, expirationTime: LocalDateTime, isSignUp: Boolean) {
  def isExpired = expirationTime.isBefore(LocalDateTime.now())
}

object UserMailToken {
  def apply(userId: UUID, isSignUp: Boolean): UserMailToken =
    UserMailToken(UUID.randomUUID(), userId, LocalDateTime.now().plusHours(24), isSignUp)
}