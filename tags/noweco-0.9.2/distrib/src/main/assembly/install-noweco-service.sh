#!/bin/bash

if [ "$(id -u)" != "0" ]; then
  echo "Noweco service installation needs root permissions."
  exit
fi

DIRNAME=$(dirname $0)
if [ "$DIRNAME" = "." ]; then
  DIRNAME="$PWD"
fi

# create /etc/init.d/noweco
NOWECO_SERVICE_NAME="noweco"
NOWECO_SERVICE_PATH="/etc/init.d/$NOWECO_SERVICE_NAME"
echo "#!/bin/sh -e" > $NOWECO_SERVICE_PATH
echo "export NOWECO_HOME=\"$DIRNAME\"" >> $NOWECO_SERVICE_PATH
echo "case \$1 in" >> $NOWECO_SERVICE_PATH
echo "start)" >> $NOWECO_SERVICE_PATH
echo "  \$NOWECO_HOME/noweco.sh start" >> $NOWECO_SERVICE_PATH
echo "  ;;" >> $NOWECO_SERVICE_PATH
echo "stop)" >> $NOWECO_SERVICE_PATH
echo "  \$NOWECO_HOME/noweco.sh stop" >> $NOWECO_SERVICE_PATH
echo "  ;;" >> $NOWECO_SERVICE_PATH
echo "esac" >> $NOWECO_SERVICE_PATH

chmod +x $NOWECO_SERVICE_PATH

service $NOWECO_SERVICE_NAME start
