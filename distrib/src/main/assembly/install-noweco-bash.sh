#!/bin/bash

DIRNAME=$(dirname $0)
if [ "$DIRNAME" = "." ]; then
  DIRNAME="$PWD"
fi

echo '_noweco() { local cur; cur=${COMP_WORDS[COMP_CWORD]}; COMPREPLY=($(compgen -W "start stop restart" -- $cur )); }' > ~/noweco.bash_profile
echo 'complete -F _noweco noweco' >> ~/noweco.bash_profile
alias noweco='sudo env JAVA_HOME="$JAVA_HOME" JAVA_OPTS="$JAVA_OPTS" NOWECO_DEBUG="$NOWECO_DEBUG" "/Users/gaellalire/apps/noweco/noweco.sh"'

echo alias noweco="'"sudo env JAVA_HOME='"$JAVA_HOME"' JAVA_OPTS='"$JAVA_OPTS"' NOWECO_DEBUG='"$NOWECO_DEBUG"' '"'$DIRNAME/noweco.sh'"'"'" >> ~/noweco.bash_profile
echo "source ~/noweco.bash_profile" >> ~/.bash_profile
chmod +x "$DIRNAME/noweco.sh"
source ~/noweco.bash_profile
