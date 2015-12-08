package models.services

import java.util.UUID

import forms.AddEventForm
import models.Event

import scala.concurrent.Future

trait EventService {

  def getDisciplineOptions: Future[Seq[(String, String)]]

  def saveNewEvent(addEventData: AddEventForm.Data, ownerId: UUID): Future[Long]

  def getEventDetails(eventId: Long): Future[Event]

  def getEventOwnerId(eventId: Long): Future[Option[UUID]]

}
