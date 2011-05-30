---------------------
Install
---------------------

Run following commands :
$ chmod +x noweco.sh

---------------------
Use
---------------------

Noweco is controlled by noweco.sh script.

Following command are available :
$ sudo -E ./noweco.sh &
$ sudo -E ./noweco.sh stop

---------------------
Debug
---------------------

When starting or restarting Noweco read the $NOWECO_DEBUG variable.
So to enable debug run :
$ export NOWECO_DEBUG=1
$ sudo -E ./noweco.sh

And to disable debug
$ unset NOWECO_DEBUG
$ sudo -E ./noweco.sh stop
$ sudo -E ./noweco.sh
