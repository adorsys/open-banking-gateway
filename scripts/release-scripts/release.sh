#!/bin/bash
set -e

# Ensure GitHub CLI is installed
if ! command -v gh &> /dev/null
then
  echo "GitHub CLI (gh) not found. Installing..."
  sudo apt update
  sudo apt install -y gh
else
  echo "GitHub CLI is already installed."
fi

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ -f "${SCRIPT_PATH}/.version.sh" ]]
then
  # shellcheck source=.version.sh
  source "${SCRIPT_PATH}/.version.sh"
else
	VERSION="UNKNOWN VERSION"
fi

# shellcheck source=release.argbash.generated.sh
source "${SCRIPT_PATH}/release.argbash.generated.sh"

if [[ "${VERBOSE}" = "on" ]]
then
  export OUT=/dev/stdout
else
  export OUT=/dev/null
fi

if [[ -f "${SCRIPT_PATH}/.common-util.sh" ]]
then
  # shellcheck source=.common-util.sh
	source "${SCRIPT_PATH}/.common-util.sh" >> ${OUT}
else
	echo 'Missing file .common-util.sh. Aborting'
	exit 1
fi

print_message "Release scripts (release, version: ${VERSION})"

RELEASE_BRANCH=$(format_release_branch_name "${RELEASE_VERSION}")

if [[ ! "${CURRENT_BRANCH}" = "${DEVELOP_BRANCH}" ]]
then
  echo "Please checkout the branch '${DEVELOP_BRANCH}' before processing this release script."
  exit 1
fi

if ! is_workspace_clean
then
  echo "This script is only safe when your have a clean workspace."
  echo "Please clean your workspace by stashing or committing and pushing changes before processing this script."
  exit 1
fi

git_checkout_existing_branch "${DEVELOP_BRANCH}"
git_pull "${REMOTE_REPO}"

# check and create master branch if not present
if is_branch_existing "${MASTER_BRANCH}" || is_branch_existing "remotes/${REMOTE_REPO}/${MASTER_BRANCH}"
then
  git_checkout_existing_branch "${MASTER_BRANCH}"
  git_pull "${REMOTE_REPO}"
else
  git_checkout_new_branch "${MASTER_BRANCH}"
  git push --set-upstream "${REMOTE_REPO}" "${MASTER_BRANCH}"
fi

git_checkout_existing_branch "${DEVELOP_BRANCH}"
git_checkout_new_branch "${RELEASE_BRANCH}"

build_snapshot_modules >> ${OUT}
cd "${GIT_REPO_DIR}"
git_reset

if [[ -f "${SCRIPT_PATH}/.release-scripts-hooks.sh" ]]
then
  source "${SCRIPT_PATH}/.release-scripts-hooks.sh"
  set_modules_version "${RELEASE_VERSION}" "${NO_SNAPSHOTS_FLAG}"
else
	echo 'Missing file .release-scripts-hooks.sh. Aborting'
	exit 1
fi

cd "${GIT_REPO_DIR}"

if ! is_workspace_clean
then
  # commit release versions
  RELEASE_COMMIT_MESSAGE=$(get_release_commit_message "${RELEASE_VERSION}")
  git_commit "${RELEASE_COMMIT_MESSAGE}"
else
  print_message "Nothing to commit..."
fi
git push --set-upstream "${REMOTE_REPO}" "${RELEASE_BRANCH}"

build_release_modules >> ${OUT}
cd "${GIT_REPO_DIR}"
git_reset

# merge current develop (over release branch) into master
git_checkout_existing_branch "${MASTER_BRANCH}"
git_merge_theirs "${RELEASE_BRANCH}"

# create release tag on master
RELEASE_TAG=$(format_release_tag "${RELEASE_VERSION}")
if [[ ! "${RELEASE_TAG}" =~ ^v ]]; then
    RELEASE_TAG="v${RELEASE_TAG}"
fi
RELEASE_TAG_MESSAGE=$(get_release_tag_message "${RELEASE_VERSION}")
git tag -a "${RELEASE_TAG}" -m "${RELEASE_TAG_MESSAGE}"
git push "${REMOTE_REPO}" "${RELEASE_TAG}"

# Create GitHub release
if git show-ref --tags | grep -q "${RELEASE_TAG}"; then
  gh release create "${RELEASE_TAG}" --title "Release ${RELEASE_VERSION}" --notes "Release notes for ${RELEASE_VERSION}"
else
  echo "No tag ${RELEASE_TAG} found. Cannot create GitHub release."
fi

# merge release into develop
git_checkout_existing_branch "${DEVELOP_BRANCH}"
git_merge_theirs "${RELEASE_BRANCH}"

# prepare next snapshot version if necessary
if [[ "${SNAPSHOTS}" = "on" ]]
then
  NEXT_SNAPSHOT_VERSION=$(format_snapshot_version "${NEXT_VERSION}")
  set_modules_version "${NEXT_SNAPSHOT_VERSION}" >> ${OUT}
fi

cd "${GIT_REPO_DIR}"

if ! is_workspace_clean
then
  # Commit next snapshot versions into develop
  SNAPSHOT_COMMIT_MESSAGE=$(get_next_snapshot_commit_message "${NEXT_SNAPSHOT_VERSION}")
  git_commit "${SNAPSHOT_COMMIT_MESSAGE}"
else
  print_message "Nothing to commit..."
fi

if git_try_merge "${RELEASE_BRANCH}"
then
  # Nope, doing that automatically is too dangerous. But the command is great!
  print_message "# Okay, now you've got a new tag and commits on ${MASTER_BRANCH} and ${DEVELOP_BRANCH}."
  print_message "# Please check if everything looks as expected and then push."
  print_message "# Use this command to push all at once or nothing, if anything goes wrong:"
  print_message "git push --atomic ${REMOTE_REPO} ${MASTER_BRANCH} ${DEVELOP_BRANCH} --follow-tags # all or nothing"
else
  print_message "# Okay, you have got a conflict while merging onto ${DEVELOP_BRANCH}"
  print_message "# but don't panic, in most cases you can easily resolve the conflicts (in some cases you even do not need to merge all)."
  print_message "# Please do so and finish the release process with the following command:"
  print_message "git push --atomic ${REMOTE_REPO} ${MASTER_BRANCH} ${DEVELOP_BRANCH} --follow-tags # all or nothing"
fi
