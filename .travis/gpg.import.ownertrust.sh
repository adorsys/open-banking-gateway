#!/usr/bin/env bash
if [ ! -z $GPG_SECRET_KEY ]
then
    echo $GPG_OWNERTRUST | base64 --decode | $GPG_EXECUTABLE --import-ownertrust
fi
