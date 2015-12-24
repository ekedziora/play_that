package models.services

import java.util.UUID

import forms.AddEventForm
import models.Event

import scala.concurrent.Future

trait EventService {


  def getEventsList: Future[Seq[Event]]

  /**
    * Gets all discipline as options for option dropdown view element
    *
    * @return sequence of tuples containing description id and it's name key for messages api
    */
  def getDisciplineOptions: Future[Seq[(String, String)]]

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

  /**
    * Gets id of event owner by specified event id
    *
    * @param eventId event id
    * @return event owner id or None if no event with specified id exists
    */
  def getEventOwnerId(eventId: Long): Future[UUID]

  /**
    * Updates event with specified id
    *
    * @param eventId event id
    * @param eventData data for update
    * @return number of updated events
    */
  def updateEvent(eventId: Long, eventData: AddEventForm.Data): Future[Int]

}
