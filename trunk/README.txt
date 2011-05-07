----------------- COMPILE -
You can simply build the project with following command :
mvn clean install -Dmaven.test.skip

If you want to run the test, you need a lotus notes connection then run the following commands :
export NOWECO_LOTUS_USERNAME=_your_user_name_
export NOWECO_LOTUS_PASSWORD=_your_password_
export NOWECO_LOTUS_URL=_url_to_lotus_login_page_
# Next 2 lines can be skipped if you have a direct access to internet
export NOWECO_PROXY_HOST=_proxy_host_
export NOWECO_PROXY_PORT=_proxy_port_
mvn clean install

----------------- DEVELOP -
Noweco use UTF-8 charset encoding.
Before using 'release.sh' script you have to add in file '~/.m2/settings.xml' the following server

    <server>
      <id>googlecode</id>
      <username>$GOOGLE_CODE_USERNAME</username>
      <password>$GOOGLE_CODE_PASSWORD</password>
    </server>
