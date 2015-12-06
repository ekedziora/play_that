package models.daos

import javax.inject.Inject

import forms.AddEventForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

class EventDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends EventDao with DAOSlick {

  import driver.api._

  override def insertNewEvent(newEventData: AddEventForm.Data): Future[Long] = {
    val insertAction = eventsQuery.map { event =>
      (event.title, event.description, event.dateTime, event.maxParticipants, event.ownerId, event.disciplineId)
    } returning eventsQuery.map(_.id) +=
     ((newEventData.title, newEventData.description, newEventData.dateTime, newEventData.maxParticipants, newEventData.ownerId, newEventData.disciplineId))

    db.run(insertAction)
  }

}
