#!/usr/bin/env bash

if [[ $GITHUB_REPOSITORY != "adorsys/open-banking-gateway" || $GITHUB_EVENT_NAME == "pull_request" ]]; then
  echo "ERROR: Documentation deployment for this build not allowed"
  exit 1
fi

TAG_REGEX="refs/tags/.+"
if [[ $GITHUB_REF =~ $TAG_REGEX ]]; then
    split=(${GITHUB_REF//\// })
    GITHUB_TAG=${split[2]}
else
  echo "Can't parse tag name from $GITHUB_REF"
  exit 1
fi


if [ ! "$GITHUB_TAG" ];
then
  GITHUB_TAG="develop"
fi

docker run -it --rm -v "$PWD":/src -w /src -u "$(id -u "${USER}"):$(id -g "${USER}")" --env GITHUB_TAG g0lden/mkdocs make site || exit 1

echo -e "Publishing Documentation...\n"

git clone --quiet --branch=gh-pages https://"$GITHUB_TOKEN"@github.com/"$GITHUB_REPOSITORY" gh-pages > /dev/null
cd gh-pages || exit 1

if [ "$GITHUB_TAG" == "develop" ];
then
  rm -Rf ./doc/develop;
  mkdir -p ./doc/develop && cp -Rf ../site/* ./doc/develop
else
(
  mkdir -p ./doc/"$GITHUB_TAG" && cp -Rf ../site/* ./doc/"$GITHUB_TAG"
  cd doc || exit 1
  rm latest
  ln -s "$GITHUB_TAG" latest
)
fi

git config --global user.email "github-actions@github.org"
git config --global user.name "$GITHUB_ACTOR"
git add -f .
git commit -m "Latest doc on successful travis build $GITHUB_RUN_NUMBER for tag $GITHUB_TAG auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Documentation to gh-pages.\n"
