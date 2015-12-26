package utils

import models.User
import play.api.i18n.Messages
import play.twirl.api.Html
import views.html.mails

import scala.language.implicitConversions

object Mailer {

  implicit def html2String(html: Html): String = html.toString

  def welcome(user: User, link: String)(implicit ms: MailService, m: Messages) {
    ms.sendEmailAsync(user.email.get)(
      subject = Messages("mail.welcome.subject"),
      bodyHtml = mails.welcome(user.username.get, link),
      bodyText = mails.welcomeTxt(user.username.get, link)
    )
  }

  def forgotPassword(email: String, link: String)(implicit ms: MailService, m: Messages) {
    ms.sendEmailAsync(email)(
      subject = Messages("mail.forgotpwd.subject"),
      bodyHtml = mails.forgotPassword(email, link),
      bodyText = mails.forgotPasswordTxt(email, link)
    )
  }

}