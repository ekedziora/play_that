package models.services

import forms.AddEventForm

import scala.concurrent.Future

trait EventService {

  def getDisciplineOptions: Future[Seq[(String, String)]]

  def saveNewEvent(addEventData: AddEventForm.Data): Future[Long]

}
