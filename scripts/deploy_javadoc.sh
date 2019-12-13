#!/usr/bin/env bash

TRAVIS_TAG="test"

if [[ $TRAVIS_REPO_SLUG != "adorsys/open-banking-gateway"
    || $TRAVIS_JDK_VERSION != "openjdk8"
    || $TRAVIS_PULL_REQUEST != "false"
    || ! $TRAVIS_TAG ]];
then
  return
fi

mvn clean javadoc:aggregate -P javadoc

echo -e "Publishing javadoc...\n"

git clone --quiet --branch=gh-pages https://"$GITHUB_TOKEN"@github.com/"$TRAVIS_REPO_SLUG" gh-pages > /dev/null
cd gh-pages || exit

(
  mkdir -p ./javadoc/"$TRAVIS_TAG" && cp -Rf ../target/site/apidocs/* ./javadoc/"$TRAVIS_TAG"
  cd javadoc || exit
  rm latest
  ln -s "$TRAVIS_TAG" latest
)

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git add -f .
git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER for tag $TRAVIS_TAG auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Javadoc to gh-pages.\n"
