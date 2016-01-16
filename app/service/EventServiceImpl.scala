package service

import java.util.UUID
import javax.inject.Inject

import forms.ParticipantsPresenceForm.ParticipantPresence
import forms.{AddEventForm, ListFiltersForm}
import models.daos.{DisciplineDao, EventDao}
import models.{Event, EventWithParticipants, ParticipantsPresence}
import play.api.libs.concurrent.Execution.Implicits._
import utils.ValidationException

import scala.concurrent.Future

class EventServiceImpl @Inject() (disciplineDAO: DisciplineDao, eventDao: EventDao) extends EventService {

  override def savePresenceReport(eventId: Long, participantsPresence: List[ParticipantPresence]): Future[Int] = {
    val presenceMap = participantsPresence.groupBy(_.present).mapValues { presences =>
      presences.map { presence =>
        presence.userId
      }
    }

    eventDao.updateEventPresenceReported(eventId, presenceReported = true).flatMap { updatedEventsCount =>
      val futures = presenceMap.map { case (present, usersIds) =>
        eventDao.updateParticipantsPresence(eventId, usersIds, present)
      }
      Future.sequence(futures).map(_.sum)
    }
  }

  override def getParticipantsPresence(eventId: Long): Future[Seq[ParticipantsPresence]] = {
    eventDao.getParticipantsPresence(eventId)
  }

  override def addParticipant(eventId: Long, userId: UUID): Future[Boolean] = {
    eventDao.addParticipant(eventId, userId).map { addedRowsCount =>
      addedRowsCount > 0
    }
  }

  override def removeParticipant(eventId: Long, userId: UUID): Future[Boolean] = {
    eventDao.removeParticipant(eventId, userId).map { removedRowsCount =>
      removedRowsCount > 0
    }
  }

  override def deleteEvent(eventId: Long): Future[Int] = {
    eventDao.deleteEvent(eventId)
  }

  override def getEventsList(filters: ListFiltersForm.Data): Future[Seq[Event]] = {
    eventDao.getEventsList(filters)
  }

  override def getDisciplineOptions: Future[Seq[(String, String)]] = {
    disciplineDAO.getDisciplinesOptions.map { optionsSequence =>
      optionsSequence.map { case (id, name) =>
        (id.toString, name)
      }
    }
  }

  override def getDisciplineOptionsForFilter: Future[Seq[(String, String)]] = {
    disciplineDAO.getDisciplinesOptions.map { optionsSequence =>
      optionsSequence.map { case (id, name) =>
        (id.toString, name)
      }.+:(("", "all"))
    }
  }

  override def saveNewEvent(addEventData: AddEventForm.Data, ownerId: UUID): Future[Long] = {
    eventDao.insertNewEvent(addEventData, ownerId)
  }

  override def getEventWithParticipants(eventId: Long): Future[EventWithParticipants] = {
    eventDao.getEventWithParticipants(eventId)
  }

  override def getEventDetails(eventId: Long): Future[Event] = {
    eventDao.getEventDetails(eventId)
  }

  override def getEventOwnerId(eventId: Long): Future[Option[UUID]] = {
    eventDao.getEventOwnerId(eventId)
  }

  override def updateEvent(eventId: Long, eventData: AddEventForm.Data): Future[Int] = {
    eventData.maxParticipants.map { maxParticipants =>
      eventDao.getNumberOfParticipants(eventId).flatMap { participantsCount =>
        if (participantsCount > maxParticipants) {
          Future.failed(ValidationException.createWithValidationMessageKey("participants.number.over.max"))
        } else {
          eventDao.updateEvent(eventId, eventData)
        }
      }
    } getOrElse {
      eventDao.updateEvent(eventId, eventData)
    }
  }

}
