#!/usr/bin/env bash
# ********************** INFO *********************
# This file is used to override default settings.
# Therefore only the functions that deviate from
# provided defaults may be left here.
# You can use it also in parent directory with the
# name .release-scripts-hook.sh
# *************************************************
set -e


# Hook method to format your release tag
# Parameter $1 - version as text
# Returns tag as text
function format_release_tag {
  echo "$1"
}

# Hook method to format your next snapshot version
# Parameter $1 - version as text
# Returns snapshot version as text
function format_snapshot_version {
  echo "$1-SNAPSHOT"
}

# Hook method to define the remote repository name
# Returns the name of the remote repository as text
function get_remote_repo_name {
  echo "origin"
}

# Hook method to define the develop branch name
# Returns the develop branch name as text
function get_develop_branch_name {
  if [[ "$1" =~ ^18\..* ]]
  then
    echo "support-18.x"
  else
    echo "develop"
  fi
}

# Hook method to define the master branch name
# Returns the master branch name as text
function get_master_branch_name {
  if [[ "$1"  =~ ^18\..* ]]
  then
    echo "master-18.x"
  else
    echo "master"
  fi
}

# Hook method to format the release branch name
# Parameter $1 - version as text
# Returns the formatted release branch name as text
function format_release_branch_name {
  echo "release-$1"
}

# Hook method to format the hotfix branch name
# Parameter $1 - version as text
# Returns the formatted hotfix branch name as text
function format_hotfix_branch_name {
  echo "hotfix-$1"
}

# Hook to build the snapshot modules before release
# You can build and run your tests here to avoid releasing an unstable build
function build_snapshot_modules {
	MAVEN_OPTS="-Dlogging.level.org.springframework.boot.autoconfigure=ERROR" \
    mvn clean install \
      -DskipTests \
      -Dspring.main.web-application-type=none \
      -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration \
      -q
}

# Hook to build the released modules after release
# You can deploy your artifacts here
function build_release_modules {
  echo "Running release build..." >&2
  echo "Command: mvn clean install ..." >&2
  MAVEN_OPTS="-Dlogging.level.org.springframework.boot.autoconfigure=ERROR" \
  mvn clean install \
    -DskipTests \
    -Dspring.main.web-application-type=none \
    -Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration \
    -q
}


# Should set version numbers in your modules
# Parameter $1 - version as text
function set_modules_version {
  local new_version="$1"

  echo "Setting version to ${new_version} in main modules"
  mvn versions:set -DnewVersion="${new_version}"

  echo "Setting version to ${new_version} in firefly-exporter"
  pushd firefly-exporter > /dev/null
  ../mvnw versions:set -DnewVersion="${new_version}"
  popd > /dev/null
}