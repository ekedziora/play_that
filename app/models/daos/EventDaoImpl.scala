package models.daos

import java.util.UUID
import javax.inject.Inject

import forms.{AddEventForm, ListFiltersForm}
import models.{Event, EventWithParticipants, Participant, ParticipantsPresence}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._
import scala.concurrent.{Future, _}

class EventDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends EventDao with DAOSlick {

  import driver.api._

  override def updateEventPresenceReported(eventId: Long, presenceReported: Boolean): Future[Int] = {
    val action = eventsQuery
      .filter(_.id === eventId)
      .map(_.presenceReported)
      .update(presenceReported)

    db.run(action)
  }

  override def updateParticipantsPresence(eventId: Long, usersIds: List[UUID], present: Boolean): Future[Int] = {
    val action = eventParticipantsQuery
      .filter(_.eventId === eventId)
      .filter(_.userId inSet usersIds)
      .map(_.present)
      .update(Some(present))

    db.run(action)
  }

  override def getParticipantsPresence(eventId: Long): Future[Seq[ParticipantsPresence]] = {
    val query = eventParticipantsQuery.filter(_.eventId === eventId) join slickUsers on (_.userId === _.id)

    db.run(query.result).map { tuples =>
      tuples.map { tuple =>
        val participant = tuple._1
        val user = tuple._2
        ParticipantsPresence(user.userID, user.username, user.getFullName, false)
      }
    }
  }

  override def getNumberOfParticipants(eventId: Long): Future[Int] = {
    val query = eventParticipantsQuery.filter(_.eventId === eventId).length
    db.run(query.result)
  }

  override def addParticipant(eventId: Long, userId: UUID): Future[Int] = {
    val insertAction = eventParticipantsQuery.map { participants =>
      (participants.eventId, participants.userId)
    } += ((eventId, userId))

    db.run(insertAction)
  }

  override def removeParticipant(eventId: Long, userId: UUID): Future[Int] = {
    val deleteAction = eventParticipantsQuery.filter(_.userId === userId).filter(_.eventId === eventId).delete

    db.run(deleteAction)
  }

  override def deleteEvent(eventId: Long): Future[Int] = {
    val deleteAction = eventsQuery.filter(_.id === eventId).delete
    db.run(deleteAction)
  }

  override def getAllEvents: Future[Seq[Event]] = {
    val query = eventsQuery join slickUsers on(_.ownerId === _.id) join sportDisciplinesQuery on(_._1.disciplineId === _.id)
    db.run(query.result) map { seq =>
      seq.map { tuple =>
        val dbEvent = tuple._1._1
        val dbUser = tuple._1._2
        val dbDiscipline = tuple._2
        new Event(dbEvent.id, dbEvent.title, dbEvent.description, dbEvent.dateTime, dbEvent.maxParticipants, dbUser.userID,
          dbUser.username, dbUser.getFullName, dbDiscipline.id, dbDiscipline.nameKey)
      }
    }
  }

  override def getEventsList(filters: ListFiltersForm.Data): Future[Seq[Event]] = {
    val query = eventsQuery.filter { event =>
      List(
        filters.disciplineId.map(event.disciplineId === _),
        filters.title.map(title => event.title like s"%$title%"),
        filters.spotsAvailable.map(_ => event.eventParticipants.countDistinct < event.maxParticipants.getOrElse(Int.MaxValue))
      ).collect({case Some(criteria)  => criteria})
        .reduceLeftOption(_ && _)
        .getOrElse(true: Rep[Boolean])
    } join slickUsers on(_.ownerId === _.id) join sportDisciplinesQuery on(_._1.disciplineId === _.id)

    db.run(query.result) map { events => {
        events.map { tuple =>
          val dbEvent = tuple._1._1
          val dbUser = tuple._1._2
          val dbDiscipline = tuple._2
          new Event(dbEvent.id, dbEvent.title, dbEvent.description, dbEvent.dateTime, dbEvent.maxParticipants, dbUser.userID,
            dbUser.username, dbUser.getFullName, dbDiscipline.id, dbDiscipline.nameKey)
        }.filter { event =>
          val fromCondition = filters.dateTimeFrom.map { from =>
            event.dateTime.isAfter(from)
          }.getOrElse(true)

          val toCondition = filters.dateTimeTo.map { to =>
            event.dateTime.isBefore(to)
          }.getOrElse(true)

          fromCondition && toCondition
        }
      }
    }
  }

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

  override def getEventWithParticipants(eventId: Long): Future[EventWithParticipants] = {
    val query = eventsQuery.filter(_.id === eventId) join slickUsers on (_.ownerId === _.id) join sportDisciplinesQuery on (_._1.disciplineId === _.id)

    val future = db.run(query.result.headOption)

    Await.result(future, 500.millis).map { tuple =>
      val q2 = eventParticipantsQuery.filter(_.eventId === tuple._1._1.id) join slickUsers on (_.userId === _.id)
      db.run(q2.result).map { participantsSeq =>
        val dbEvent = tuple._1._1
        val dbUser = tuple._1._2
        val dbDiscipline = tuple._2
        val participants = participantsSeq.map { participant =>
          val dbParticipant = participant._1
          val dbUserParticipant = participant._2
          new Participant(dbParticipant.id, dbUserParticipant.userID, dbUserParticipant.username, dbUserParticipant.getFullName)
        }
        new EventWithParticipants(dbEvent.id, dbEvent.title, dbEvent.description, dbEvent.dateTime, dbEvent.maxParticipants, dbUser.userID,
          dbUser.username, dbUser.getFullName, dbDiscipline.id, dbDiscipline.nameKey, dbEvent.presenceReported, participants)
      }
    }.getOrElse {
      throw utils.NotFoundException()
    }
  }

  override def getEventOwnerId(eventId: Long): Future[Option[UUID]] = {
    val query = eventsQuery.filter(_.id === eventId).map(_.ownerId)
    db.run(query.result.headOption)
  }

  override def updateEvent(eventId: Long, eventData: AddEventForm.Data): Future[Int] = {
    val query = eventsQuery
      .filter(_.id === eventId)
      .map(e => (e.title, e.description, e.dateTime, e.maxParticipants, e.disciplineId))
      .update((eventData.title, eventData.description, eventData.dateTime, eventData.maxParticipants, eventData.disciplineId))

    db.run(query)
  }

}
