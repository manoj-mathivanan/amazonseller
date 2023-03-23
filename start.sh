#!/bin/bash

ps -ef |grep java | grep -v grep | awk '{print $2}'|xargs kill
nohup mvn spring-boot:run > logs.txt 2>&1 & disown