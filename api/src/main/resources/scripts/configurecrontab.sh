#!/bin/bash

INSTALL_DIR=src/main/resources
LOG=$HOME/emt.log
#SYSTEM_ID=`hostname`-`ifconfig eth0 | grep HWaddr | awk '{ print $NF}' | sed 's/://g'`
#NOW=`date +%Y%m%d-%H%M%S`


(crontab -l ; echo "1,16,31,46 * * * * $INSTALL_DIR/pcheartbeat.sh") | crontab -
(crontab -l ; echo "@reboot $INSTALL_DIR/pcstartup.sh") | crontab -
#(crontab -l ; echo "2,17,32,47 * * * * $INSTALL_DIR/openmrs-heartbeat.sh") | crontab -


