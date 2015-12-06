package models.daos

import forms.AddEventForm

import scala.concurrent.Future

trait EventDao {

  def insertNewEvent(newEventData: AddEventForm.Data): Future[Long]

}
