---------------------
Use
---------------------

Run noweco.bat
Closing the windows will stop noweco, you can also run
$ noweco.bat stop

---------------------
Debug
---------------------

When starting Noweco read the %NOWECO_DEBUG% variable.
So to enable debug run :
$ set NOWECO_DEBUG=1
$ noweco.bat

And to disable debug
$ ^CTRL-C
$ set NOWECO_DEBUG=
$ noweco.bat stop
$ noweco.bat
