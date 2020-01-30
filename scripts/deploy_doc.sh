#!/usr/bin/env bash

if [[ $TRAVIS_REPO_SLUG != "adorsys/open-banking-gateway"
    || $TRAVIS_JDK_VERSION != "openjdk8"
    || $TRAVIS_PULL_REQUEST != "false" ]];
then
  echo "ERROR: Documentation deployment for this build not allowed"
  exit 1
fi

if [ ! "$TRAVIS_TAG" ];
then
  TRAVIS_TAG="develop"
fi

docker run -it --rm -v "$PWD":/src -w /src -u "$(id -u "${USER}"):$(id -g "${USER}")" --env TRAVIS_TAG g0lden/mkdocs make site || exit 1

echo -e "Publishing Documentation...\n"

git clone --quiet --branch=gh-pages https://"$GITHUB_TOKEN"@github.com/"$TRAVIS_REPO_SLUG" gh-pages > /dev/null
cd gh-pages || exit 1

if [ "$TRAVIS_TAG" == "develop" ];
then
  rm -Rf ./doc/develop;
  mkdir -p ./doc/develop && cp -Rf ../site/* ./doc/develop
else
(
  mkdir -p ./doc/"$TRAVIS_TAG" && cp -Rf ../site/* ./doc/"$TRAVIS_TAG"
  cd doc || exit 1
  rm latest
  ln -s "$TRAVIS_TAG" latest
)
fi

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git add -f .
git commit -m "Latest doc on successful travis build $TRAVIS_BUILD_NUMBER for tag $TRAVIS_TAG auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Documentation to gh-pages.\n"
