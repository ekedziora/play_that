package models.services

import java.util.UUID
import javax.inject.Inject

import models.UserMailToken
import models.daos.MailTokenDao

import scala.concurrent.Future

class MailTokenServiceImpl @Inject()(mailTokenDao: MailTokenDao) extends MailTokenService {

  override def create(token: UserMailToken): Future[UUID] = {
    mailTokenDao.saveMailToken(token)
  }

  override def retrieve(id: UUID): Future[Option[UserMailToken]] = {
    mailTokenDao.getMailToken(id)
  }

  override def consume(id: UUID): Future[Int] = {
    mailTokenDao.deleteMailToken(id)
  }
}
