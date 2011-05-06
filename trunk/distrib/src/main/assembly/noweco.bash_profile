_noweco() { local cur; cur=${COMP_WORDS[COMP_CWORD]}; COMPREPLY=($(compgen -W 'start stop restart' -- $cur )); }
complete -F _noweco noweco
alias noweco="'"sudo $DIRNAME/noweco.sh"'"