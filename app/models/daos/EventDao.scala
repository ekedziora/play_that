package models.daos

import java.util.UUID

import forms.AddEventForm
import models.{Event, EventWithParticipants}

import scala.concurrent.Future

trait EventDao {

  /**
    * Adds participant to event
    *
    * @param eventId event id
    * @param userId user id
    * @return number of added participants
    */
  def addParticipant(eventId: Long, userId: UUID): Future[Int]

  /**
    * Deletes event with specified id
    *
    * @param eventId id of event
    * @return number of affected rows
    */
  def deleteEvent(eventId: Long): Future[Int]

  def getAllEvents: Future[Seq[Event]]

  /**
    * Inserts new event
    *
    * @param newEventData event data
    * @param ownerId id of user which created event
    * @return inserted event id
    */
  def insertNewEvent(newEventData: AddEventForm.Data, ownerId: UUID): Future[Long]

  /**
    * Gets event details by specified event id
    *
    * @param eventId event id
    * @return event details
    */
  def getEventDetails(eventId: Long): Future[Event]

  def getEventWithParticipants(eventId: Long): Future[EventWithParticipants]

  /**
    * Gets id of event owner by specified event id
    *
    * @param eventId event id
    * @return id of event owner or None if no event with specified id exists
    */
  def getEventOwnerId(eventId: Long): Future[Option[UUID]]

  /**
    * Updates event with specified id
    *
    * @param eventId event id
    * @param eventData data for update
    * @return number of updated events
    */
  def updateEvent(eventId: Long, eventData: AddEventForm.Data): Future[Int]

}
