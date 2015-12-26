package models.services

import java.util.UUID
import javax.inject.Inject

import forms.AddEventForm
import models.Event
import models.daos.{DisciplineDao, EventDao}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class EventServiceImpl @Inject() (disciplineDAO: DisciplineDao, eventDao: EventDao) extends EventService {

  override def getEventsList: Future[Seq[Event]] = {
    eventDao.getAllEvents
  }

  override def getDisciplineOptions: Future[Seq[(String, String)]] = {
    disciplineDAO.getDisciplinesOptions.map { optionsSequence =>
      optionsSequence.map { case (id, name) =>
        (id.toString, name)
      }
    }
  }

  override def saveNewEvent(addEventData: AddEventForm.Data, ownerId: UUID): Future[Long] = {
    eventDao.insertNewEvent(addEventData, ownerId)
  }

  override def getEventDetails(eventId: Long): Future[Event] = {
    eventDao.getEventDetails(eventId)
  }

  override def getEventOwnerId(eventId: Long): Future[Option[UUID]] = {
    eventDao.getEventOwnerId(eventId)
  }

  override def updateEvent(eventId: Long, eventData: AddEventForm.Data): Future[Int] = {
    eventDao.updateEvent(eventId, eventData)
  }

}
