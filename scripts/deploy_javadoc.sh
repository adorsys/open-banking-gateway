#!/usr/bin/env bash

TAG_REGEX="refs/tags/.+"
if [[ $GITHUB_REF =~ $TAG_REGEX ]]; then
    split=(${GITHUB_REF//\// })
    GITHUB_TAG=${split[2]}
else
  echo "Can't parse tag name from $GITHUB_REF"
  exit 1
fi

if [[ $GITHUB_REPOSITORY != "adorsys/open-banking-gateway"
    || $GITHUB_EVENT_NAME == "pull_request"
    || ! $GITHUB_TAG ]];
then
  echo "ERROR: Javadoc deployment for this build not allowed"
  exit 1
fi

mvn clean javadoc:aggregate -P javadoc --no-transfer-progress

echo -e "Publishing javadoc...\n"

git clone --quiet --branch=gh-pages https://"$GITHUB_TOKEN"@github.com/"$GITHUB_REPOSITORY" gh-pages > /dev/null
cd gh-pages || exit 1

(
  mkdir -p ./javadoc/"$GITHUB_TAG" && cp -Rf ../target/site/apidocs/* ./javadoc/"$GITHUB_TAG"
  cd javadoc || exit 1
  rm latest
  ln -s "$GITHUB_TAG" latest
)

git config --global user.email "github-actions@github.org"
git config --global user.name "$GITHUB_ACTOR"
git add -f .
git commit -m "Latest javadoc on successful travis build $GITHUB_RUN_NUMBER for tag $GITHUB_TAG auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Javadoc to gh-pages.\n"
