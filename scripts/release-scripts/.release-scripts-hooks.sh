#!/bin/bash
# ********************** INFO *********************
# This file is used to override default settings.
# Therefore only the functions that deviate from
# provided defaults may be left here.
# Actually this settings are used to release this release_scripts itself.
# *************************************************
set -e


# Should set version numbers in your modules
# Parameter $1 - version as text
function set_modules_version {
  # The following line is used for release process of release-scripts itself:
  sed -i .versionBackup "s/\(export VERSION=\)[0-9a-zA-Z.-]*\( \#\)/\1$1\2/" .version.sh
}
