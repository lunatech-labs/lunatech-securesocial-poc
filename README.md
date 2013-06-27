SecureSocial PoC
================
This is a PoC application to see how to use Play 2/Scala, Slick and SecureSocial together.

Adding OAuth secret codes
-------------------------
SecureSocial requires keys and secrets to authenticate with OAuth providers. We obviously can't commit those.
To add these secrets to your configuration, copy `secrets.sample.conf` to `secrets.conf` and fill in the blanks.
The `secrets.conf` file is included from application.conf, and not from `securesocial.conf`, since it also contains the Play application secret.
