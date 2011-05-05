#!/bin/bash

if [ "$(id -u)" != "0" ]; then
  echo "Noweco listen on privileged ports and needs root permissions."
  exit
fi

DIRNAME=`dirname $0`
if [ "$DIRNAME" = "." ]; then
  DIRNAME="$PWD"
fi

STOP_NOWECO=false
START_NOWECO=false

COMMAND=$1
case "$COMMAND" in
  start)
    START_NOWECO=true    
  ;;
  stop)
    STOP_NOWECO=true
  ;;
  restart)
    STOP_NOWECO=true
    START_NOWECO=true
  ;;
  *)
    echo "Usage"
    echo "  start : start Noweco"
    echo "  stop : stop Noweco"
    echo "  restart : stop then start Noweco"
  ;;
esac

if [ "$STOP_NOWECO" = "true" ]; then
  if [ -f "$DIRNAME/noweco.pid" ]; then
    NOWECO_PID=$(cat "$DIRNAME/noweco.pid")
    PS_NOWECO_PID=$(ps -p $NOWECO_PID | wc -l)
    if [ $PS_NOWECO_PID = 2 ]; then
      kill $NOWECO_PID
    else
      echo "Noweco process not launched";
      exit
    fi
    rm "$DIRNAME/noweco.pid"
  else
    echo "Noweco process not launched";
    exit
  fi
fi

if [ "$START_NOWECO" = "true" ]; then
  if [ -f "$DIRNAME/noweco.pid" ]; then
    NOWECO_PID=$(cat "$DIRNAME/noweco.pid")
    PS_NOWECO_PID=$(ps -p $NOWECO_PID | wc -l)
    # 2 = HEADER + PID
    if [ $PS_NOWECO_PID = 2 ]; then
      echo "Noweco process already launched";
      exit
    fi
    rm "$DIRNAME/noweco.pid"
  fi
  
  CLASSPATH=""
  for file in $(find "$DIRNAME/lib" -name '*.jar' -print)
  do
    if [ -z "$CLASSPATH" ]; then
      CLASSPATH="$file"
    else
      CLASSPATH="$CLASSPATH:$file"
    fi
  done
  if [ "$JAVA_OPTS" = "" ]; then
    JAVA_OPTS=""
  fi
  if [ "$NOWECO_DEBUG" != "" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
  fi
  nohup java $JAVA_OPTS -cp $CLASSPATH -Dlogback.configurationFile="$DIRNAME/logback.xml" com.googlecode.noweco.cli.NowecoCLI "$DIRNAME" 1>"$DIRNAME/logs/out.console" 2>"$DIRNAME/logs/err.console" &
  NOWECO_PID=$!
  echo $NOWECO_PID > "$DIRNAME/noweco.pid"
fi


