#!/bin/bash
set -e

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCRIPT_PARENT_PATH="$( dirname "${SCRIPT_PATH}" )"

# shellcheck source=.hooks-default.sh
source "${SCRIPT_PATH}/.hooks-default.sh"

if [ -f "${SCRIPT_PARENT_PATH}/.release-scripts-hooks.sh" ]; then
	echo "Found .release-scripts-hooks.sh. Using it as master hooks"

	# shellcheck source=.release-scripts-hooks.sh
	source "${SCRIPT_PARENT_PATH}/.release-scripts-hooks.sh"
fi

REMOTE_REPO=$(get_remote_repo_name)
export REMOTE_REPO

DEVELOP_BRANCH=$(get_develop_branch_name "${RELEASE_VERSION}")
export DEVELOP_BRANCH

MASTER_BRANCH=$(get_master_branch_name "${RELEASE_VERSION}")
export MASTER_BRANCH

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
export CURRENT_BRANCH

GIT_REPO_DIR=$(git rev-parse --show-toplevel)
export GIT_REPO_DIR

function is_branch_existing {
  if ! git branch -a --list | grep -q "$1"
  then
    return 1
  else
    return 0
  fi
}

function is_workspace_clean {
  if git diff-files --quiet --ignore-submodules --
  then
    return 0
  else
    return 1
  fi
}

function is_workspace_synced {
  if test "$(git rev-parse "@{u}")" = "$(git rev-parse HEAD)"
  then
    return 0
  else
    return 1
  fi
}

function print_message {
  if [[ "${QUIET}" = "off" ]]
  then
    echo "${1}"
  fi
}

function git_commit {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git commit -am "${1}"
  else
    git commit --quiet -am "${1}"
  fi
}

function git_checkout_existing_branch {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git checkout "${1}"
  else
    git checkout --quiet "${1}"
  fi
}

function git_checkout_new_branch {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git checkout -b "${1}"
  else
    git checkout --quiet -b "${1}"
  fi
}

function git_pull {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git pull "${1}"
  else
    git pull --quiet "${1}"
  fi
}

function git_reset {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git reset --hard
  else
    git reset --quiet --hard
  fi
}

function git_merge_theirs {
  if [[ "${VERBOSE}" = "on" ]]
  then
    git merge -X theirs --no-edit "${1}"
  else
    git merge --quiet -X theirs --no-edit "${1}"
  fi
}

function git_try_merge {
  if [[ "${VERBOSE}" = "on" ]]; then
    git merge --no-edit "${1}"
  else
    git merge --quiet --no-edit "${1}"
  fi
}
