package models

import securesocial.core._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.Identity
import securesocial.core.UserId
import securesocial.core.OAuth1Info
import play.api.Play.current

case class User(uid: Option[Long] = None,
                id: UserId,
                firstName: String,
                lastName: String,
                fullName: String,
                email: Option[String],
                avatarUrl: Option[String],
                authMethod: AuthenticationMethod,
                oAuth1Info: Option[OAuth1Info],
                oAuth2Info: Option[OAuth2Info],
                passwordInfo: Option[PasswordInfo] = None) extends Identity {

}

object User {

  def apply(i: Identity): User = User(None, i.id, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info)
}

object Users extends Table[User]("user") {

  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
    authenticationMethod => authenticationMethod.method,
    string => AuthenticationMethod(string)
  )

  implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])) = tuple match {
    case (Some(token), Some(secret)) => Some(OAuth1Info(token, secret))
    case _ => None
  }

  implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])) = tuple match {
    case (Some(token), tokenType, expiresIn, refreshToken) => Some(OAuth2Info(token, tokenType, expiresIn, refreshToken))
    case _ => None
  }

  implicit def tuple2UserId(tuple: (String, String)) = tuple match {
    case (userId, providerId) => UserId(userId, providerId)
  }


  def uid = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[String]("userId")

  def providerId = column[String]("providerId")

  def email = column[Option[String]]("email")

  def firstName = column[String]("firstName")

  def lastName = column[String]("lastName")

  def fullName = column[String]("fullName")

  def authMethod = column[AuthenticationMethod]("authMethod")

  def avatarUrl = column[Option[String]]("avatarUrl")

  // oAuth 1
  def token = column[Option[String]]("token")

  def secret = column[Option[String]]("secret")

  // oAuth 2
  def accessToken = column[Option[String]]("accessToken")

  def tokenType = column[Option[String]]("tokenType")

  def expiresIn = column[Option[Int]]("expiresIn")

  def refreshToken = column[Option[String]]("refreshToken")

  def * = uid.? ~ userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken <>(
    u => User(u._1, (u._2, u._3), u._4, u._5, u._6, u._7, u._8, u._9, (u._10, u._11), (u._12, u._13, u._14, u._15)),
    (u: User) => Some((u.uid, u.id.id, u.id.providerId, u.firstName, u.lastName, u.fullName, u.email, u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret), u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn), u.oAuth2Info.flatMap(_.refreshToken))))

  def autoInc = * returning uid

  def findById(id: Long) = DB.withSession {
    implicit session =>
      val q = for {
        user <- Users
        if user.uid is id
      } yield user

      q.firstOption
  }

  def findByUserId(userId: UserId): Option[User] = DB.withSession {
    implicit session =>
      val q = for {
        user <- Users
        if (user.userId is userId.id) && (user.providerId is userId.providerId)
      } yield user

      q.firstOption
  }

  def all = DB.withSession {
    implicit session =>
      val q = for {
        user <- Users
      } yield user

      q.list
  }

  def save(i: Identity): User = this.save(User(i))

  def save(user: User) = DB.withSession {
    implicit session =>
      findByUserId(user.id) match {
        case None => {
          val uid = this.autoInc.insert(user)
          user.copy(uid = Some(uid))
        }
        case Some(existingUser) => {
          val userRow = for {
            u <- Users
            if u.uid is existingUser.uid
          } yield u

          val updatedUser = user.copy(uid = existingUser.uid)
          userRow.update(updatedUser)
          updatedUser
        }
      }
  }


}
