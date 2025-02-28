#!/bin/bash

# -plain을 제외한 JAR 파일 선택
BUILD_JAR=$(ls /home/ubuntu/app/build/libs/*SNAPSHOT.jar | grep -v 'plain')
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

# build 파일 복사
DEPLOY_PATH=/home/ubuntu/app/
if [ -f "$BUILD_JAR" ]; then
  echo ">>> build 파일 복사" >> /home/ubuntu/deploy.log
  cp $BUILD_JAR $DEPLOY_PATH
else
  echo ">>> ERROR: 빌드 파일을 찾을 수 없습니다: $BUILD_JAR" >> /home/ubuntu/deploy_err.log
  exit 1
fi

# 현재 실행중인 애플리케이션 pid 확인 후 종료
CURRENT_PID=$(pgrep -f "$JAR_NAME")
if [ -n "$CURRENT_PID" ]; then
  echo ">>> 현재 실행 중인 애플리케이션($CURRENT_PID)을 종료합니다." >> /home/ubuntu/deploy.log
  kill -15 $CURRENT_PID
  sleep 5
else
  echo ">>> 실행 중인 애플리케이션이 없습니다." >> /home/ubuntu/deploy.log
fi

# DEPLOY_JAR 배포
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포" >> /home/ubuntu/deploy.log
echo ">>> $DEPLOY_JAR의 $JAR_NAME를 실행합니다" >> /home/ubuntu/deploy.log

cd /home/ubuntu/app
nohup java -jar $DEPLOY_JAR > >(ts '[%Y-%m-%d %H:%M:%S]' >> /home/ubuntu/deploy.log) 2> >(ts '[%Y-%m-%d %H:%M:%S]' >> /home/ubuntu/deploy_err.log) &
