package provider

import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.providers.oauth2.BaseGoogleProvider
import com.mohiva.play.silhouette.impl.providers.{OAuth2Settings, OAuth2StateProvider}

class CustomGoogleProvider(
                              protected val httpLayer: HTTPLayer,
                              protected val stateProvider: OAuth2StateProvider,
                              val settings: OAuth2Settings)
  extends BaseGoogleProvider with CustomSocialProfileBuilder {

  type Self = CustomGoogleProvider

  val profileParser = new CustomGoogleProfileParser

  /**
    * Gets a provider initialized with a new settings object.
    *
    * @param f A function which gets the settings passed and returns different settings.
    * @return An instance of the provider initialized with new settings.
    */
  def withSettings(f: (Settings) => Settings) = {
    new CustomGoogleProvider(httpLayer, stateProvider, f(settings))
  }
}