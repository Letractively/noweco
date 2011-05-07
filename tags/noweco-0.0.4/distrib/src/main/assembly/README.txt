---------------------
Install
---------------------

Run following commands :
$ chmod +x install-noweco-bash.sh
$ ./install-noweco-bash.sh

---------------------
Use
---------------------

Noweco is controlled via noweco alias (which sudo the noweco.sh script).

Following command are available :
$ noweco start
$ noweco stop
$ noweco restart

---------------------
Debug
---------------------

When starting or restarting Noweco read the $NOWECO_DEBUG variable.
So to enable debug run :
$ export NOWECO_DEBUG=1
$ noweco start

And to disable debug
$ unset NOWECO_DEBUG
$ noweco restart
