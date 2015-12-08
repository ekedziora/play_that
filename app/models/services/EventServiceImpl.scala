package models.services

import javax.inject.Inject

import forms.AddEventForm
import models.Event
import models.daos.{DisciplineDao, EventDao}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class EventServiceImpl @Inject() (disciplineDAO: DisciplineDao, eventDao: EventDao) extends EventService {

  override def getDisciplineOptions: Future[Seq[(String, String)]] = {
    disciplineDAO.getDisciplinesOptions.map { optionsSequence =>
      optionsSequence.map { case (id, name) =>
        (id.toString, name)
      }
    }
  }

  override def saveNewEvent(addEventData: AddEventForm.Data): Future[Long] = {
    eventDao.insertNewEvent(addEventData)
  }

  override def getEventDetails(eventId: Long): Future[Event] = {
    eventDao.getEventDetails(eventId)
  }

}
