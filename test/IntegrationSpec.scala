package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import org.fluentlenium.core.FluentPage

import org.fluentlenium.adapter.FluentTest
import org.specs2.execute.Result
import org.openqa.selenium.WebDriver
import java.util.concurrent.TimeUnit

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {

  val driver = HTMLUNIT

  "Register" should {

    "should register successfully with valid email address" in {
      running(TestServer(3333), driver) { browser =>

        val registerPage = new RegisterPage(browser.webDriver)
        val loginPage = new LoginPage(browser.webDriver)

        browser.goTo(registerPage)
        registerPage.fillAndSubmitForm("user@example.com")

        browser.await().atMost(5, TimeUnit.SECONDS).untilPage(loginPage).isLoaded
        loginPage.mustBeAt
      }
    }

    "should not register with invalid email address input" in {
      running(TestServer(3333), driver) { browser =>
        val registerPage = new RegisterPage(browser.webDriver)

        browser.goTo(registerPage)
        registerPage.fillAndSubmitForm("I am not a valid email address")
        registerPage.mustBeAt
      }
    }

  } // Register

  def getDefaultBaseUrl: String = "http://localhost:3333"

  class RegisterPage(webDriver:WebDriver) extends FluentPage(webDriver) {
    override def getUrl() : String = getDefaultBaseUrl + "/signup"

    def mustBeAt : Result = {
      title() must be_==("Sign Up")
    }

    def fillAndSubmitForm(email:String) {
      fill("input#email").`with`(email)

      click("button.btn-primary")
    }
  }

  class LoginPage(webDriver:WebDriver) extends FluentPage(webDriver) {
    override def getUrl() : String = getDefaultBaseUrl + "/login"

    def mustBeAt : Result = {
      title() must be_==("Login")
    }
  }
}