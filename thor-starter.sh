#!/bin/bash

# -------------------------------
# Utility Functions
# -------------------------------

check_maven() {
  if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH."
    echo "Please install Maven first."
    exit 1
  fi
}

install_project() {
  local dir="$1"
  local name="$2"

  if [ -d "$dir" ]; then
    echo "➡️ Installing $name from $dir ..."
    cd "$dir" || { echo "❌ Failed to enter $dir"; exit 1; }
    mvn clean install > /dev/null
    if [ $? -ne 0 ]; then
      echo "❌ mvn install failed for $name"
      exit 1
    fi
    cd - > /dev/null
  else
    echo "❌ $name directory not found at $dir"
    exit 1
  fi
}

prompt_project_details() {
  read -p "Enter Group ID (default: com.sgx): " GROUP_ID
  read -p "Enter Artifact ID: " ARTIFACT_ID
  read -p "Enter Version (default: 1.0-SNAPSHOT): " VERSION
  read -p "Enter Package (default: same as Group ID): " PACKAGE

  GROUP_ID=${GROUP_ID:-com.sgx}
  VERSION=${VERSION:-1.0-SNAPSHOT}
  PACKAGE=${PACKAGE:-$GROUP_ID}

  echo "$GROUP_ID" "$ARTIFACT_ID" "$VERSION" "$PACKAGE"
}

generate_project() {
  local groupId="$1"
  local artifactId="$2"
  local version="$3"
  local package="$4"

  local ARCHETYPE_GROUP_ID="com.sgx"
  local ARCHETYPE_ARTIFACT_ID="thor-archetype"
  local ARCHETYPE_VERSION="1.0-SNAPSHOT"

  echo "➡️ Generating Maven project..."
  mvn archetype:generate \
    -DarchetypeGroupId=$ARCHETYPE_GROUP_ID \
    -DarchetypeArtifactId=$ARCHETYPE_ARTIFACT_ID \
    -DarchetypeVersion=$ARCHETYPE_VERSION \
    -DgroupId=$groupId \
    -DartifactId=$artifactId \
    -Dversion=$version \
    -Dpackage=$package \
    -DinteractiveMode=false

  echo "✅ Project generated successfully in ./$artifactId"
}

# -------------------------------
# Main Script Execution
# -------------------------------

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

check_maven

install_project "$SCRIPT_DIR/thor-parent" "parent project"
install_project "$SCRIPT_DIR/thor-archetype" "custom archetype"

read GROUP_ID ARTIFACT_ID VERSION PACKAGE < <(prompt_project_details)

generate_project "$GROUP_ID" "$ARTIFACT_ID" "$VERSION" "$PACKAGE"
