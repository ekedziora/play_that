package models.daos

import java.util.UUID
import javax.inject.Inject

import models.UserMailToken
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class MailTokenDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends MailTokenDao with DAOSlick {

  import driver.api._

  override def saveMailToken(newToken: UserMailToken): Future[UUID] = {
    val insertAction = userMailTokensQuery.map { token =>
      (token.id, token.userId, token.expirationTime, token.isSignUp)
    } returning userMailTokensQuery.map(_.id) +=
      ((newToken.id, newToken.userId, newToken.expirationTime, newToken.isSignUp))

    db.run(insertAction)
  }

  override def getMailToken(id: UUID): Future[Option[UserMailToken]] = {
    val query = userMailTokensQuery.filter(_.id === id)

    db.run(query.result.headOption).map { optionDbToken =>
      optionDbToken.map { dbToken =>
        UserMailToken(dbToken.id, dbToken.userId, dbToken.expirationTime, dbToken.isSignUp)
      }
    }
  }

  override def deleteMailToken(id: UUID): Future[Int] = {
    val deleteAction = userMailTokensQuery.filter(_.id === id).delete

    db.run(deleteAction)
  }

}
