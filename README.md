# SecureSocial Activator Template

This is an application to see how to use Play 2/Scala, Slick and SecureSocial together.  It uses Slick 2.0.0 and an
in memory H2 database by default.

[SecureSocial](http://securesocial.ws) is an authentication plugin on [Play](http://playframework.com) that allows users
to login through a variety of methods, including through OAuth providers.

The blog post is at [http://blog.lunatech.com/2013/07/04/play-securesocial-slick](http://blog.lunatech.com/2013/07/04/play-securesocial-slick)

## Adding OAuth secret codes

SecureSocial requires keys and secrets to authenticate with OAuth providers. We obviously can't commit those.
To add these secrets to your configuration, copy `secrets.sample.conf` to `secrets.conf` and fill in the blanks.
The `secrets.conf` file is included from application.conf, and not from `securesocial.conf`, since it also contains the Play application secret.

Configuration of SecureSocial is at [http://securesocial.ws/guide/configuration.html](http://securesocial.ws/guide/configuration.html)

## Setup and running

### Common setup:

1. Copy `conf/secrets.sample.conf` to `conf/secrets.conf`
1. Generate an application secret in `application.secret` (easiest way is to create a new Play app, then copy it)
1. Set up your mail server or leave `smtp.mock=true` set in secrets.conf (you will have to look at console for output).

To run with Activator:

1. [Download Activator](http://typesafe.com/platform/getstarted) or `brew install typesafe-activator` or `./activator`
1. `activator ui`

To run natively:

1. Make sure you have Play 2.2.1 installed.
1. `play run`

Running with OAuth providers:

Here, we need to use a callback, so we use ngrok to expose the internal service, and don't really need nginx (but it's
a good habit to use it).

1. Register for any external OAuth services you want to authenticate.
1. Put the oauth credentials into `secrets.conf`. 
1. Modify `play.plugins` to use the OAuth plugins you registered (a complete list is in `play.plugins.withoauth`).
1. Download and install nginx (`brew install nginx` if you're on a Mac).
1. Download and install ngrok from https://ngrok.com -- this allows OAuth callbacks to happen if need be.
1. Start the play server (as above).
1. Start ngrok with `ngrok 8080`.
1. Modify the server_name in `conf/nginx.conf` to point to your ngrok URL (i.e. ei3fawekemf.ngrok.com)
1. Copy `nginx.conf` to the nginx configuration directory with appropriate file name, i.e. /usr/local/etc/nginx/play.conf
1. Start nginx with `nginx -c /usr/local/etc/nginx/play.conf` (to stop, use `nginx -s stop`).
1. Go to the public URL of your application, i.e. http://ei3fawekemf.ngrok.com
1. Go to Twitter, enter the callback URL as http://ei3fawekemf.ngrok.com/authenticate/twitter
1. Try to login with Twitter, etc.

Note that you can also use HTTPS with ngrok and it will be correctly set up with a valid SSL certificate (which makes
SSL testing much easier).  You can also examine HTTP requests in ngrok by going to [http://localhost:4040/](http://localhost:4040/).
