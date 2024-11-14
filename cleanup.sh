#!/bin/bash
# 로그 파일들이 존재하면 삭제
if [ -f /home/ubuntu/app/logs/log.log ]; then
  sudo rm /home/ubuntu/app/logs/log.log
fi

if [ -f /home/ubuntu/app/logs/err_log.log ]; then
  sudo rm /home/ubuntu/app/logs/err_log.log
fi
