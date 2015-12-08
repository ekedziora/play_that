package models.daos

import forms.AddEventForm
import models.Event

import scala.concurrent.Future

trait EventDao {

  def insertNewEvent(newEventData: AddEventForm.Data): Future[Long]

  def getEventDetails(eventId: Long): Future[Event]

}
