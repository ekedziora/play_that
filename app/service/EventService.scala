package service

import java.util.UUID

import forms.{AddEventForm, ListFiltersForm}
import models.{Event, EventWithParticipants}

import scala.concurrent.Future

trait EventService {

  /**
    * Adds user with specified id to given event with id
    *
    * @param eventId event id
    * @param userId user id
    * @return true if participant was added, false otherwise
    */
  def addParticipant(eventId: Long, userId: UUID): Future[Boolean]

  /**
    * Removes user with specified id from event with given id
    *
    * @param eventId event id
    * @param userId user id
    * @return true if participant was removed, false otherwise
    */
  def removeParticipant(eventId: Long, userId: UUID): Future[Boolean]

  /**
    * Deletes event with specified id
    *
    * @param eventId id of event
    * @return number of affected rows
    */
  def deleteEvent(eventId: Long): Future[Int]

  /**
    * Gets all events
    *
    * @return sequence of all events
    */
  def getEventsList(filters: ListFiltersForm.Data): Future[Seq[Event]]

  /**
    * Gets all disciplines as options for option dropdown view element
    *
    * @return sequence of tuples containing description id and it's name key for messages api
    */
  def getDisciplineOptions: Future[Seq[(String, String)]]

  /**
    * Gets all disciplines as options and all option for option dropdown view element
    *
    * @return sequence of tuples containing description id and it's name key for messages api
    */
  def getDisciplineOptionsForFilter: Future[Seq[(String, String)]]

  /**
    * Saves new event based on data and ownerId
    *
    * @param addEventData data of new event
    * @param ownerId id of user who created event
    * @return id of created event
    */
  def saveNewEvent(addEventData: AddEventForm.Data, ownerId: UUID): Future[Long]

  /**
    * Gets details of event with specified id
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
    * @return event owner id or None if no event with specified id exists
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
