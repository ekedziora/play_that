package models.daos

import java.util.UUID

import forms.{AddEventForm, ListFiltersForm}
import models.{Event, EventWithParticipants, ParticipantsPresence}

import scala.concurrent.Future

trait EventDao {

  /**
    * Updates event presence reported value
    *
    * @param eventId event id
    * @param presenceReported if presence was reported
    * @return number of updated events
    */
  def updateEventPresenceReported(eventId: Long, presenceReported: Boolean): Future[Int]

  /**
    * Updates presence of participants from specified event and with id included in list
    *
    * @param eventId event id
    * @param usersIds list of users ids
    * @param present if participant was present
    * @return number of update participants
    */
  def updateParticipantsPresence(eventId: Long, usersIds: List[UUID], present: Boolean): Future[Int]

  /**
    * Gets participants presence object to fill
    *
    * @param eventId event id
    * @return empty participants presence object
    */
  def getParticipantsPresence(eventId: Long): Future[Seq[ParticipantsPresence]]

  /**
    * Gets number of event participants
    *
    * @param eventId event id
    * @return number of participants for event
    */
  def getNumberOfParticipants(eventId: Long): Future[Int]

  /**
    * Adds participant to event
    *
    * @param eventId event id
    * @param userId user id
    * @return number of added participants
    */
  def addParticipant(eventId: Long, userId: UUID): Future[Int]

  /**
    * Remove participant from event
    *
    * @param eventId event id
    * @param userId user id
    * @return number of removed participants
    */
  def removeParticipant(eventId: Long, userId: UUID): Future[Int]

  /**
    * Deletes event with specified id
    *
    * @param eventId id of event
    * @return number of affected rows
    */
  def deleteEvent(eventId: Long): Future[Int]

  /**
    * Gets all saved events
    *
    * @return all saved events
    */
  def getAllEvents: Future[Seq[Event]]

  /**
    * Gets events filtered by specified filters
    *
    * @param filters filters for events list
    * @return sequence of filtered events
    */
  def getEventsList(filters: ListFiltersForm.Data): Future[Seq[Event]]

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
