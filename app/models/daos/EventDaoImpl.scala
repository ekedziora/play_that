package models.daos

import java.util.UUID
import javax.inject.Inject

import forms.AddEventForm
import models.Event
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class EventDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends EventDao with DAOSlick {

  import driver.api._

  override def insertNewEvent(newEventData: AddEventForm.Data, ownerId: UUID): Future[Long] = {
    val insertAction = eventsQuery.map { event =>
      (event.title, event.description, event.dateTime, event.maxParticipants, event.ownerId, event.disciplineId)
    } returning eventsQuery.map(_.id) +=
     ((newEventData.title, newEventData.description, newEventData.dateTime, newEventData.maxParticipants, ownerId, newEventData.disciplineId))

    db.run(insertAction)
  }

  override def getEventDetails(eventId: Long): Future[Event] = {
    val query = eventsQuery join slickUsers on(_.ownerId === _.id) join sportDisciplinesQuery on(_._1.disciplineId === _.id) filter(_._1._1.id === eventId)
    db.run(query.result.headOption) map { option =>
      option.map { tuple =>
        val dbEvent = tuple._1._1
        val dbUser = tuple._1._2
        val dbDiscipline = tuple._2
        new Event(dbEvent.id, dbEvent.title, dbEvent.description, dbEvent.dateTime, dbEvent.maxParticipants, dbUser.userID,
          dbUser.username, dbUser.getFullName, dbDiscipline.id, dbDiscipline.nameKey)
      } getOrElse(throw utils.NotFoundException())
    }
  }

}
