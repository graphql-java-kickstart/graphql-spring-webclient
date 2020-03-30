#!/bin/bash
set -ev

saveGitCredentials() {
    cat >$HOME/.netrc <<EOL
machine github.com
login ${GITHUB_USERNAME}
password ${GITHUB_TOKEN}

machine api.github.com
login ${GITHUB_USERNAME}
password ${GITHUB_TOKEN}
EOL
    chmod 600 $HOME/.netrc
}

if [ "${TRAVIS_PULL_REQUEST}" = "false" ] && [ "${TRAVIS_BRANCH}" = "master" ]; then
#  if [ "${RELEASE}" = "true" ]; then
    echo "Deploying release to Bintray"
#    NEXT_VERSION="$(gradle properties -q | grep "version:" | grep -v "kotlin_version:" | awk '{print $2}' | tr -d '[:space:]')b$(date +%Y%m%d%H%M)"
    NEXT_VERSION="$(gradle properties -q | grep "version:" | grep -v "kotlin_version:" | awk '{print $2}' | tr -d '[:space:]')b${TRAVIS_BUILD_NUMBER}"
    sed -i -E "s/^version(\s)?=.*/version=${NEXT_VERSION}/" gradle.properties
    saveGitCredentials
    ./gradlew clean assemble && ./gradlew check --info && ./gradlew bintrayUpload -x check --info
#  else
#    echo "Deploying snapshot"
#    saveGitCredentials
#    ./gradlew artifactoryPublish -Dsnapshot=true -Dbuild.number="${TRAVIS_BUILD_NUMBER}"
#  fi
else
    echo "Verify"
    ./gradlew clean assemble && ./gradlew check --info
fi
