package models.daos

import java.util.UUID

import models.UserMailToken

import scala.concurrent.Future

trait MailTokenDao {

  def saveMailToken(token: UserMailToken): Future[UUID]

  def getMailToken(id: UUID): Future[Option[UserMailToken]]

  def deleteMailToken(id: UUID): Future[Int]

}
