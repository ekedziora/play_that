package provider

import com.mohiva.play.silhouette.impl.providers.SocialProfileBuilder

trait CustomSocialProfileBuilder {
  self: SocialProfileBuilder =>

  type Profile = CustomSocialProfile
}