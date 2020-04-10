#!/bin/bash

TYPE="$1"
BRANCH="$2"
DIR="$3"
USER="$4"

################ check lock
if test -f "./.lock"; then
  echo "LOCK. NOW UPDATING."
  exit 0
fi
################

################ check params
if [ -z "$TYPE" ]; then
  echo "ERROR: Type params type script"
  exit 1
fi

if [ -z "$BRANCH" ]; then
  echo "ERROR: Branch require"
  exit 1
fi

if [ -z "$DIR" ]; then
  echo "ERROR: Dir project require"
  exit 1
fi

if [ -z "$USER" ]; then
  echo "ERROR: User email oauth require"
  exit 1
fi
################

cd "$DIR" || exit

################ check brach
git pull --quiet >/dev/null
findBrach=$(git branch -a | grep "remotes/origin/$BRANCH")
if [ -z "$findBrach" ]; then
  echo "ERROR: Branch not found"
  exit 1
fi
################

################ exclude merge
git reset --hard HEAD
git checkout "$BRANCH" --quiet
git pull --quiet
################

/scripts/server/lock.sh

case "$TYPE" in
--tag)
  echo "-> START TAG"

  ## up patch version
  newTag=$(/snap/bin/deployer --path="$DIR" --version-name-up)

  ## add tag
  git tag "$newTag" && git push origin master --tags --quiet >/dev/null

  ## commmit changes
  git add . >/dev/null && git commit -m "server - up name" >/dev/null && git push --quiet >/dev/null

  echo "-> ADD NEWS TAG SUCCESSFULY ($newTag)"
  /scripts/server/lock.sh
  ;;
--production)
  echo "-> START PRODUCTION"
  echo "START build upload"

  ## get pass
  pass="$(...)"

  ## get applicationId
  applicationId=$(/snap/bin/deployer --path="$DIR" --get-application-id)

  ## build app
  {
    ## up versionCode
    /snap/bin/deployer --path="$DIR" --version-code-up

    ## build
    ./gradlew --console=plain bundleRelease -Pandroid.injected.signing.store.password="$pass" -Pandroid.injected.signing.key.password="$pass" -Pandroid.injected.signing.store.file="$DIR/KEY/$pass.jks" -Pandroid.injected.signing.key.alias="$applicationId" >.lock
  } &>/dev/null

  ## get log
  gradlewBuil=$(cat .lock)

  if [[ $gradlewBuil == *"BUILD SUCCESSFUL"* ]]; then
    echo "BUILD SUCCESSFUL"
    ## upload
    /snap/bin/deployer --path-build="$DIR/app/build/outputs/bundle/release/app-release.aab" --upload-track=production --user-email="$USER" --mailing
    ## commmit changes
    git add . >/dev/null && git commit -m "server - upload production" >/dev/null && git push --quiet >/dev/null
  else
    echo "BUILD ERROR"
  fi

  echo "-> END PRODUCTION"
  /scripts/server/lock.sh
  ;;
--internal)
  echo "-> START INTERNAL"
  echo "START build upload"

  ## get pass
  pass="$(...)"

  ## get applicationId
  applicationId=$(/snap/bin/deployer --path="$DIR" --get-application-id)

  ## build app
  {
    ## up versionCode
    /snap/bin/deployer --path="$DIR" --version-code-up

    ## build
    ./gradlew --console=plain assembleForTest -Pandroid.injected.signing.store.password="$pass" -Pandroid.injected.signing.key.password="$pass" -Pandroid.injected.signing.store.file="$DIR/KEY/$pass.jks" -Pandroid.injected.signing.key.alias="$applicationId" >.lock
  } &>/dev/null

  ## get log
  gradlewBuil=$(cat .lock)

  if [[ $gradlewBuil == *"BUILD SUCCESSFUL"* ]]; then
    echo "BUILD SUCCESSFUL"
    ## upload
    slackNote=$(eval /scripts/server/internal_commits.sh)
    /snap/bin/deployer --path-build="$DIR/app/build/outputs/apk/forTest/app-forTest.apk" --upload-track=internal --user-email="$USER" --mailing --note-add-version --mailing-slack-desc="$slackNote"
    ## commmit changes
    git add . >/dev/null && git commit -m "server - upload internal" >/dev/null && git push --quiet >/dev/null
  else
    echo "BUILD ERROR"
  fi

  echo "-> END INTERNAL"
  /scripts/server/lock.sh
  ;;
--changelog)
  echo "-> START CHANGELOG"

  ## update CHANGELOG.md
  /snap/bin/deployer --path="$DIR" --changelog

  ## commmit changes
  git add . >/dev/null && git commit -m "server - changelog" >/dev/null && git push --quiet >/dev/null

  echo ""
  echo "-> END CHANGELOG"
  /scripts/server/lock.sh
  ;;
esac

if test -f "./.lock"; then
  rm "./.lock"
  exit 0
fi