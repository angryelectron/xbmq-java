#!/bin/bash

# XBMQ auto-update script.  Check for latest version of XBMQ.  Download
# and install if newer version exists.  Run daily/weekly from a cron job
# or as needed.
#
# Pre-requisites:  
#
#	- XBMQ is installed and running as init script
#	- this script can run as root/sudo
# 

# Path to xbmq.jar 
dir="/opt/xbmq"

# URL of the auto-update release 
src="https://github.com/angryelectron/xbmq/releases/download/autoupdate/"

ver="version.txt"
jar="xbmq.jar"

if [ -e /tmp/version.txt ]; then

	# get latest version number 
	curl -s -L $src$ver -o /tmp/latest.txt	

	# compare with installed version and exit if the same 
	if diff /tmp/version.txt /tmp/latest.txt >/dev/null; then
		exit 0
	fi
else
	# set the current version to the latest version 
	curl -s -L $src$ver -o /tmp/$ver
fi

# download and install the latest version
if curl -s -L $src$jar -o /tmp/$jar; then
	service xbmqd stop
	cp /tmp/$jar $dir/$jar
	mv /tmp/latest.txt /tmp/version.txt
	service xbmqd start
fi
