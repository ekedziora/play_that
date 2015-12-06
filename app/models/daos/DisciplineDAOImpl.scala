package models.daos

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

class DisciplineDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends DisciplineDao with DAOSlick {

  import driver.api._

  override def getDisciplinesOptions: Future[Seq[(Long, String)]] = {
    val query = sportDisciplinesQuery.map(discipline => (discipline.id, discipline.nameKey))
    db.run(query.result)
  }

}
