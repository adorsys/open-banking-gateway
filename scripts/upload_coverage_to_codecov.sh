#!/usr/bin/env bash

BRANCH_REGEX="refs/heads/.+"
if [[ $GITHUB_REF =~ $BRANCH_REGEX ]]; then
    split=(${GITHUB_REF//\// })
    GITHUB_BRANCH=${split[2]}
else
  echo "Can't parse branch name from $GITHUB_REF"
  exit 1
fi

BRANCH="$GITHUB_BRANCH"
echo "Sending test results to codecov using $BRANCH"

# Production code
bash <(curl -s https://codecov.io/bash) -s './last-module-codecoverage*' -F backend  -B "$BRANCH";
bash <(curl -s https://codecov.io/bash) -s './consent-ui*' -F frontend -B "$BRANCH";

# Example code
bash <(curl -s https://codecov.io/bash) -s './fintech-examples/fintech-last-module-codecoverage*' -F fintech  -B "$BRANCH";
bash <(curl -s https://codecov.io/bash) -s './fintech-examples/fintech-ui*' -F fintech -B "$BRANCH";
