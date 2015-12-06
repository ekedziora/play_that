package models.daos

import scala.concurrent.Future

trait DisciplineDao {

  def getDisciplinesOptions: Future[Seq[(Long, String)]]

}
