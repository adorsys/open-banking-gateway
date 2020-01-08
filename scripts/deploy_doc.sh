#!/usr/bin/env bash

if [[ $TRAVIS_REPO_SLUG != "adorsys/open-banking-gateway"
    || $TRAVIS_JDK_VERSION != "openjdk8"
    || $TRAVIS_PULL_REQUEST != "false"
    || ! $TRAVIS_TAG ]];
then
  echo "ERROR: Documentation deployment for this build not allowed"
  exit 1
fi

docker run -it --rm -v $PWD:/src -w /src -u $(id -u ${USER}):$(id -g ${USER}) --env TRAVIS_TAG g0lden/mkdocs make site

echo -e "Publishing Documentation...\n"

git clone --quiet --branch=gh-pages https://"$GITHUB_TOKEN"@github.com/"$TRAVIS_REPO_SLUG" gh-pages > /dev/null
cd gh-pages || exit

(
  mkdir -p ./doc/"$TRAVIS_TAG" && cp -Rf ../site/* ./doc/"$TRAVIS_TAG"
  cd doc || exit
  rm latest
  ln -s "$TRAVIS_TAG" latest
)

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git add -f .
git commit -m "Latest doc on successful travis build $TRAVIS_BUILD_NUMBER for tag $TRAVIS_TAG auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Documentation to gh-pages.\n"
