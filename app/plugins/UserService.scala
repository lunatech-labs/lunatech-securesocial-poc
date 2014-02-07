package plugins

import play.api.Application
import securesocial.core.{IdentityId, Identity, UserServicePlugin}
import securesocial.core.providers.Token
import models.Tables._
import org.joda.time.DateTime

class UserService(application: Application) extends UserServicePlugin(application) {

  def find(id: IdentityId) = Users.findByIdentityId(id)

  def save(user: Identity) = Users.save(user)

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = {
    Users.findByEmailAndProvider(email, providerId)
  }

  def save(token: Token) {
    Tokens.save(token)
  }

  def findToken(tokenId: String) = {
    Tokens.findById(tokenId)
  }

  def deleteToken(uuid: String) {
    Tokens.delete(uuid)
  }

  def deleteExpiredTokens() {
    Tokens.deleteExpiredTokens(DateTime.now())

  }
}
