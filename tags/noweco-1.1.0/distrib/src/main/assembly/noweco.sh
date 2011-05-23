#!/bin/bash

######################
# Arguments
#
# <empty> : start noweco
# stop : stop current noweco instance
#
######################
# System properties
# 
# JAVA_HOME : the location of the JVM
# NOWECO_DEBUG : is this variable has not empty value, the debug mode is active
# JAVA_OPTS : java system properties to be set
# NOWECO_OPTS : java system properties array to be set, if set JAVA_OPTS is ignored
######################

if [ $(id -u) -ne 0 ]; then
  echo "Noweco listen on privileged ports and needs root permissions."
  exit 1
fi

DIRNAME=`dirname $0`
if [ "$DIRNAME" = "." ]; then
  DIRNAME="$PWD"
fi

if [ "$JAVA_HOME" = "" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
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

MAIN_CLASS=com.googlecode.noweco.cli.StartNoweco
if [ "$1" == "stop" ]; then
  MAIN_CLASS=com.googlecode.noweco.cli.StopNoweco
fi
    
if [ ${#NOWECO_OPTS[@]} -eq 0 ]; then
  NOWECO_OPTS=($JAVA_OPTS)
fi
if [ ${#NOWECO_DEBUG} -ne 0 ]; then
  NOWECO_OPTS=("${NOWECO_OPTS[@]}" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000)
fi
NOWECO_OPTS=("${NOWECO_OPTS[@]}" -Dcom.sun.management.jmxremote -Dlogback.configurationFile="$DIRNAME/logback.xml" -Dnoweco.home="$DIRNAME")

"$JAVA" "${NOWECO_OPTS[@]}" -cp "$CLASSPATH" $MAIN_CLASS
