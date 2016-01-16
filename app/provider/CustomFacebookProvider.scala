package provider

import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.providers.oauth2.BaseFacebookProvider
import com.mohiva.play.silhouette.impl.providers.{OAuth2Settings, OAuth2StateProvider}

class CustomFacebookProvider(
                              protected val httpLayer: HTTPLayer,
                              protected val stateProvider: OAuth2StateProvider,
                              val settings: OAuth2Settings)
  extends BaseFacebookProvider with CustomSocialProfileBuilder {

  type Self = CustomFacebookProvider

  val profileParser = new CustomFacebookProfileParser

  val API = "https://graph.facebook.com/v2.3/me?fields=first_name,last_name,picture,email,gender,birthday&return_ssl_resources=1&access_token=%s"

  protected override val urls: Map[String, String] = Map("api" -> API)

  /**
    * Gets a provider initialized with a new settings object.
    *
    * @param f A function which gets the settings passed and returns different settings.
    * @return An instance of the provider initialized with new settings.
    */
  def withSettings(f: (Settings) => Settings) = {
    new CustomFacebookProvider(httpLayer, stateProvider, f(settings))
  }
}