package plugins

import play.api._
import securesocial.core._
import play.api.libs.ws.WS
import LinkedInOauth2Provider._
import securesocial.core.AuthenticationException
import play.api.Play.current

class LinkedInOauth2Provider(application: Application) extends OAuth2Provider(application) {
  val GetAuthenticatedUser = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,picture-url,email-address)?oauth2_access_token=%s"

  def fillProfile(user: SocialUser) = {
    val promise = WS.url(GetAuthenticatedUser.format(user.oAuth2Info.get.accessToken)).get()
    try {
      val response = awaitResult(promise)

      response.xml match {
        case error@ <error>{_*}</error> =>
          Logger.error("[securesocial] error retrieving profile information from LinkedIn. Message = %s".format((error \ Message).text))
          throw new AuthenticationException()
        case me@ <person>{_*}</person> =>
          val userId = (me \ Id).text
          val firstName = (me \ FirstName).text
          val lastName = (me \ LastName).text
          val fullName = (me \ FullName).text
          val avatarUrl = Option((me \ AvatarUrl).text).filter(_.trim.nonEmpty)
          val emailAddress = Option((me \ EmailAddress).text).filter(_.trim.nonEmpty)

          user.copy(identityId = IdentityId(userId, id),
            firstName = firstName,
            lastName = lastName,
            fullName = fullName,
            avatarUrl = avatarUrl,
            email = emailAddress)
        case _ =>
          Logger.error("[securesocial] invalid response for profile information from LinkedIn")
          Logger.debug ("[securesocial] response: %s".format(response.body))
          throw new AuthenticationException()
      }
    } catch {
      case e: Exception => {
        Logger.error("[securesocial] error retrieving profile information from LinkedIn", e)
        throw new AuthenticationException()
      }
    }
  }

  def id = LinkedIn2
}

object LinkedInOauth2Provider {
  val LinkedIn2 = "linkedin2"
  val Person = "person"
  val Id = "id"
  val FirstName = "first-name"
  val LastName = "last-name"
  val FullName = "formatted-name"
  val AvatarUrl = "picture-url"
  val EmailAddress = "email-address"
  val Message = "message"
}
