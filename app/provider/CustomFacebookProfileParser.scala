package provider

import com.mohiva.play.silhouette.impl.providers.SocialProfileParser
import com.mohiva.play.silhouette.impl.providers.oauth2.FacebookProfileParser
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue

class CustomFacebookProfileParser extends SocialProfileParser[JsValue, CustomSocialProfile] {

  val commonParser = new FacebookProfileParser

  def parse(json: JsValue) = commonParser.parse(json).map { commonProfile =>
    val gender = (json \ "gender").asOpt[String]
    val birthday = (json \ "birthday").asOpt[String]
    CustomSocialProfile(
      loginInfo = commonProfile.loginInfo,
      firstName = commonProfile.firstName,
      lastName = commonProfile.lastName,
      avatarURL = commonProfile.avatarURL,
      email = commonProfile.email.getOrElse(throw new IllegalArgumentException("For given user no valid email address was returned")),
      gender = gender,
      birthday = birthday
      )
  }
}