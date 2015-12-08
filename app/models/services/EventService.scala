package models.services

import forms.AddEventForm
import models.Event

import scala.concurrent.Future

trait EventService {

  def getDisciplineOptions: Future[Seq[(String, String)]]

  def saveNewEvent(addEventData: AddEventForm.Data): Future[Long]

  def getEventDetails(eventId: Long): Future[Event]

}
