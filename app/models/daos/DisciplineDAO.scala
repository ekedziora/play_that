package models.daos

import scala.concurrent.Future

trait DisciplineDao {

  /**
    * Gets all disciplines as options for option dropdown view element
    *
    * @return sequence of tuples containing discipline id and name key for messages api
    */
  def getDisciplinesOptions: Future[Seq[(Long, String)]]

}
