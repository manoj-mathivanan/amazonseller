#!/bin/bash

export PEPMART_HOME="/home/ma_manoj/amazonseller/"

function restart_jars() {
    #ps -ef |grep Main | grep -v grep | awk '{print $2}'| xargs kill

    cd $PEPMART_HOME
    mvn spring-boot:run > logs.txt 2>&1 & disown
}

rest_pid=`ps -ef | grep Main | grep -v grep |awk '{print $2}'`

if [[ -z "$rest_pid" ]]; then
    echo "jars are not running! Starting pepmart..."
    restart_jars
else
    echo "Pepmart is running! Exiting..."
fi