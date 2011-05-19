#!/bin/bash

function copy() {
	echo "cp -Rf $1/$2 $3"
	cp -Rf $1/$2 $3
}

PROJECT_DIR="/home/pierre-marie/workspace/sources/googlecode.com/noweco/branches/james-mailbox-webmail/src/main/resources"
INSTALLATION_DIR="./conf/"
FILES_TO_COPY="conf"


#
# CONF files
#

for f in $FILES_TO_COPY; do
	copy $PROJECT_DIR $f $INSTALLATION_DIR
done

#
# LIB files
#

LIB_FILES="/home/pierre-marie/workspace/sources/googlecode.com/noweco/branches/james-mailbox-webmail/target/apache-james-mailbox-webmail-0.0.1-SNAPSHOT.jar /home/pierre-marie/workspace/sources/svn.apache.org/james/server-trunk/container-spring/target/james-server-container-spring-3.0-M3-SNAPSHOT.jar"
LIB_DEST_DIR="./lib/"

for f in $LIB_FILES; do
	echo "cp $f $LIB_DEST_DIR"
	cp $f $LIB_DEST_DIR
done
