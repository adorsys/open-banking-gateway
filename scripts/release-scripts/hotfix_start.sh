#!/bin/bash
set -e

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ -f "${SCRIPT_PATH}/.version.sh" ]]
then
  # shellcheck source=.version.sh
	source "${SCRIPT_PATH}/.version.sh"
else
	VERSION="UNKNOWN VERSION"
fi

# shellcheck source=hotfix_start.argbash.generated.sh
source "${SCRIPT_PATH}/hotfix_start.argbash.generated.sh"

if [[ "${VERBOSE}" = "on" ]]
then
  OUT=/dev/stdout
else
  OUT=/dev/null
fi

if [[ "${SNAPSHOTS}" = "on" ]]
then
  HOTFIX_MODULE_VERSION="${HOTFIX_VERSION}-SNAPSHOT"
else
  HOTFIX_MODULE_VERSION="${HOTFIX_VERSION}"
fi

# Necessary to calculate develop/master branch name
RELEASE_VERSION=${HOTFIX_VERSION}

if [[ -f "${SCRIPT_PATH}/.common-util.sh" ]]
then
  # shellcheck source=.common-util.sh
	source "${SCRIPT_PATH}/.common-util.sh" >> ${OUT}
else
	echo 'Missing file .common-util.sh. Aborting'
	exit 1
fi

print_message "Release scripts (hotfix-start, version: ${VERSION})"

unset RELEASE_VERSION

HOTFIX_BRANCH=$(format_hotfix_branch_name "${HOTFIX_VERSION}")

if ! is_workspace_clean
then
  echo "This script is only safe when your have a clean workspace."
  echo "Please clean your workspace by stashing or committing and pushing changes before processing this script."
  exit 1
fi

git_checkout_existing_branch "${MASTER_BRANCH}"
git_pull "${REMOTE_REPO}"

git_checkout_new_branch "${HOTFIX_BRANCH}"

set_modules_version "${HOTFIX_MODULE_VERSION}" >> ${OUT}
cd "${GIT_REPO_DIR}"

if ! is_workspace_clean
then
  # commit hotfix versions
  START_HOTFIX_COMMIT_MESSAGE=$(get_start_hotfix_commit_message "${HOTFIX_MODULE_VERSION}")
  git_commit "${START_HOTFIX_COMMIT_MESSAGE}"
else
  print_message "Nothing to commit..."
fi

print_message "# Okay, now you've got a new hotfix branch called ${HOTFIX_BRANCH}"
print_message "# Please check if everything looks as expected and then push."
print_message "# Use this command to push your created hotfix-branch:"
print_message "git push --set-upstream ${REMOTE_REPO} ${HOTFIX_BRANCH}"
