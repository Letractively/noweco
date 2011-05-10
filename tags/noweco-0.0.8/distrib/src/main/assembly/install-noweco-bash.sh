#!/bin/bash

DIRNAME=$(dirname $0)
if [ "$DIRNAME" = "." ]; then
  DIRNAME="$PWD"
fi

# create noweco.bash_profile
echo '_noweco() { local cur; cur=${COMP_WORDS[COMP_CWORD]}; COMPREPLY=($(compgen -W "start stop restart" -- $cur )); }' > "$DIRNAME/noweco.bash_profile"
echo 'complete -F _noweco noweco' >> "$DIRNAME/noweco.bash_profile"
echo alias noweco="'"sudo -E '"'$DIRNAME/noweco.sh'"'"'" >> "$DIRNAME/noweco.bash_profile"

# add (or replace) inclusion of noweco.bash_profile in ~/.bash_profile
sed -i .noweco-install-backup '$a\
source "'$DIRNAME'/noweco.bash_profile"
;/noweco.bash_profile/d' ~/.bash_profile

rm ~/.bash_profile.noweco-install-backup

# make sh executable
chmod +x "$DIRNAME/noweco.sh"

# run noweco.bash_profile
source "$DIRNAME/noweco.bash_profile"
