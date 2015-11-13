Download the last noweco distribution.

Unzip it, and follow README-Unix.txt instructions

You can add following lines to your .bash\_profile to get an easier control of noweco.

```
_noweco() { local cur; cur=${COMP_WORDS[COMP_CWORD]}; COMPREPLY=($(compgen -W "start stop restart" -- $cur )); }
complete -F _noweco noweco
alias noweco='sudo -E "/path/to/noweco/noweco.sh"'
```

With this line, you can type with bash autocompletion
```
  $ noweco start
  $ noweco stop
  $ noweco restart
```