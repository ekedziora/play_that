package service

import java.util.UUID

import models.UserMailToken

import scala.concurrent.Future

trait MailTokenService {
  def create(token: UserMailToken): Future[UUID]

  def retrieve(id: UUID): Future[Option[UserMailToken]]

  def consume(id: UUID): Future[Int]
}