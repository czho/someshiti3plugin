#!/bin/sh
# shell script to prepend i3status with more stuff
i3status | while :
do
  read line
  echo "$(cat $HOME/i3data) | $line" || exit 1
done
