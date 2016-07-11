#!/bin/bash

PATH=$PATH:/sbin
#INSTALL_DIR=$HOME/Dekstop/Release/Test
LOG=$HOME/emts.log

SYSTEM_ID=`hostname`-`ifconfig eth0 | grep HWaddr | awk '{ print $NF}' | sed 's/://g'`
NOW=`date +%Y%m%d-%H%M%S`
#SYS_LOAD=`uptime | awk -F":" '{print $4}'`
#UPTIME=`uptime`
#SYS_LOAD_TMP="`echo $UPTIME | awk -F"," '{print $3}' | awk -F":" '{print $2}'` ;`echo $UPTIME | awk -F"," '{print $4}'` ;`echo $UPTIME | awk -F"," '{print $5}'`"
#SYS_LOAD=`echo $SYS_LOAD_TMP | tr -d " "`
SYS_LOAD=`uptime | sed "s/^.*load averages: //g" | sed "s/^.*load average: //g" | tr -d " " | sed "s/,/;/g"`
#SYS_LOAD2=`uptime`

echo "$NOW;$SYSTEM_ID;HEARTBEAT;$SYS_LOAD" >> $LOG

#(crontab -l ; echo "@reboot $INSTALL_DIR/testheartbeat.sh") | crontab -
