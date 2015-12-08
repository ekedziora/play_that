package models.daos

import java.util.UUID

import forms.AddEventForm
import models.Event

import scala.concurrent.Future

trait EventDao {

  def insertNewEvent(newEventData: AddEventForm.Data, ownerId: UUID): Future[Long]

  def getEventDetails(eventId: Long): Future[Event]

  def getEventOwnerId(eventId: Long): Future[Option[UUID]]

}
