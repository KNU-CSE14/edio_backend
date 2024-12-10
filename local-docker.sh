#!/bin/bash

# 스크립트 시작 로그
echo "Starting Docker Compose setup..."
echo "Running local gradle setup ..."

BUILD_FILE="build.gradle"

# DevTools 의존성
DEVTOOLS_DEP="developmentOnly 'org.springframework.boot:spring-boot-devtools'"

# Check if the dependency already exists
if grep -q "$DEVTOOLS_DEP" "$BUILD_FILE"; then
  echo "DevTools is already added to $BUILD_FILE"
else
  # Add the dependency under dependencies block
  sed -i "/dependencies {/a\    $DEVTOOLS_DEP" "$BUILD_FILE"
  echo "DevTools has been added to $BUILD_FILE"
fi

# 실행 명령어
COMMAND="docker-compose -f docker-compose.local.yaml up"

# 종료 시 작업 정의
function cleanup {
  echo "Cleaning up... Removing DevTools from $BUILD_FILE"

  # Remove the added DevTools dependency
  sed -i "/$DEVTOOLS_DEP/d" "$BUILD_FILE"

  # 확인 로그
  if ! grep -q "$DEVTOOLS_DEP" "$BUILD_FILE"; then
    echo "DevTools dependency removed from $BUILD_FILE"
  else
    echo "Failed to remove DevTools dependency from $BUILD_FILE"
  fi
}

# trap을 이용해 종료 시 cleanup 함수 실행
trap cleanup EXIT

# 실행 전 확인
echo "Running: $COMMAND"

# 명령 실행
$COMMAND


# 스크립트 종료 로그
echo "Docker Compose setup finished."
