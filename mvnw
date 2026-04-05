#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
MAVEN_VERSION="3.9.9"
MAVEN_DIST_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-${MAVEN_VERSION}"
ARCHIVE_PATH="$WRAPPER_DIR/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

if [ ! -d "$MAVEN_HOME" ]; then
  mkdir -p "$WRAPPER_DIR"
  if [ ! -f "$ARCHIVE_PATH" ]; then
    curl -fsSL "$MAVEN_DIST_URL" -o "$ARCHIVE_PATH"
  fi
  tar -xzf "$ARCHIVE_PATH" -C "$WRAPPER_DIR"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
