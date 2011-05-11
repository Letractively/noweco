#!/bin/bash

if [ $(id -u) -ne 0 ]; then
  echo "Noweco listen on privileged ports and needs root permissions."
  exit 1
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
    if [ $PS_NOWECO_PID -eq 2 ]; then
      kill $NOWECO_PID
      WAIT_COUNT=0
      # 10 seconds timeout
      while [ $WAIT_COUNT -lt 10 -a $PS_NOWECO_PID -eq 2 ]; do
        sleep 1
        PS_NOWECO_PID=$(ps -p $NOWECO_PID | wc -l)
        let WAIT_COUNT=WAIT_COUNT+1
      done     
      if [ $PS_NOWECO_PID -eq 2 ]; then
        echo "Unable to stop Noweco process"
        exit 2
      fi
    else
      echo "Noweco process not launched"
      exit 3
    fi
    rm "$DIRNAME/noweco.pid"
  else
    echo "Noweco process not launched"
    exit 3
  fi
fi

if [ "$START_NOWECO" = "true" ]; then
  if [ -f "$DIRNAME/noweco.pid" ]; then
    NOWECO_PID=$(cat "$DIRNAME/noweco.pid")
    PS_NOWECO_PID=$(ps -p $NOWECO_PID | wc -l)
    # 2 = HEADER + PID
    if [ $PS_NOWECO_PID -eq 2 ]; then
      echo "Noweco process already launched"
      exit 4
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
  
  if [ "$JAVA_HOME" = "" ]; then
    JAVA="java"
  else
    JAVA="$JAVA_HOME/bin/java"
  fi
  if [ "$NOWECO_DEBUG" != "" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
  fi
  "$JAVA" $JAVA_OPTS -cp $CLASSPATH -Dlogback.configurationFile="$DIRNAME/logback.xml" com.googlecode.noweco.cli.NowecoCLI "$DIRNAME" 1>"$DIRNAME/logs/out.console" 2>"$DIRNAME/logs/err.console" &
  NOWECO_PID=$!
  echo $NOWECO_PID > "$DIRNAME/noweco.pid"
fi
