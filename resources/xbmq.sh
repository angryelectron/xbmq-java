#!/bin/bash
DIR=`dirname "$0"`

# Make sure Java shuts down when this process is killed
trap "kill 0" EXIT

# Hack for embedded platforms which uses serial port names not recognized by
# RXTX and don't have librxtxSerial.so in java.library.path.  Currently
# supports RaspberryPi (Raspbian) and Beaglebone (Debian)
#
if [ -e "/dev/ttyAMA0" ] || [ -e "/dev/ttyO0" ]
then
  for port in `find /dev -name 'tty*'`
  do
    PORTS="$PORTS:$port"
  done
  JAVA_OPT="-Djava.library.path=/usr/lib/jni -Dgnu.io.rxtx.SerialPorts=$PORTS"
fi

(cd $DIR; java $JAVA_OPT -jar xbmq.jar $@)
