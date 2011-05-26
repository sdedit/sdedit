#
# arg1 : the name of a directory to be created or emptied
#
function init {
  if [ ! -e $1 ]
  then
    mkdir $1
  else
    rm -rf $1/*
  fi
}

#
# Prints a list of files found recursively in the current directories
# and sub directories.
#
# arg1           : a list of extensions of files to be included
# arg2 (optional): a list of substrings of files to be excluded
#
function files {
  INCLUDE=''
  EXCLUDE=''
  for TYPE in $1
  do
    if [ -n "$INCLUDE" ]
    then
      INCLUDE=$INCLUDE'|^.*\.'$TYPE'$'
    else
      INCLUDE='^.*\.'$TYPE'$'
    fi
  done
  if [ -n "$2" ]
  then
    for NAME in $2
    do
      if [ -n "$EXCLUDE" ]
      then
        EXCLUDE=$EXCLUDE'|^.*'$NAME'.*$'
      else
        EXCLUDE='^.*'$NAME'.*$'
      fi
    done
    find -L . | egrep $INCLUDE | egrep -v $EXCLUDE
  else
    find -L . | egrep $INCLUDE
  fi
}

#
# Prints the jar files found in the directory whose
# name is passed as the first argument, separated by
# ':', prefixed by the directory.
#
function start_command {
  CMD="java -classpath bin"
  for FILE in $1/*jar
  do
    CMD="$CMD:$FILE"
  done
  echo $CMD net.sf.sdedit.Main
}

#
# The same as start_command, but separates jar files by
# ';' and replaces all occurrences of '/' by '\'
#

function dos_start_command {
  CMD=$(start_command $1)
  echo $CMD | sed 's/\//\\/g' | sed 's/:/;/g'
}
