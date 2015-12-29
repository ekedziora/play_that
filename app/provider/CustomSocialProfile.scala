package provider

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.SocialProfile

case class CustomSocialProfile(loginInfo: LoginInfo,
                               firstName: Option[String] = None,
                               lastName: Option[String] = None,
                               email: String,
                               avatarURL: Option[String] = None,
                               gender: Option[String] = None,
                               birthday: Option[String] = None
                               ) extends SocialProfile
