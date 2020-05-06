#!/usr/bin/env bash

lastDate=$(eval git log -1 --grep='internal' -i -E --format=%cd HEAD)

if [ -z "$lastDate" ]; then
  lastDate=$(git rev-list --max-parents=0 --format=%cd HEAD | sed -n 2p)
fi

logs=$(git log -30 --after="$lastDate" --grep='\[Feature\]|\[Bug\]|\[Change\]|\[Test\]' -i -E --format='* '%B\(%an\) HEAD | sed -e ":a;$!{N;s/\n/ /;ba;}" | sed -e "s/*/\n/g")

readarray -t array <<<"$logs"
for i in "${array[@]}"; do
  if [ -z "$i" ]; then
    continue
  fi
  IFS='[' read -ra parse <<<"$i"
  trim=$(echo "${parse[0]}" | xargs)
  echo "* <https://youtrack.pinxterapp.com/issue/$trim|*$trim*> [${parse[1]}"
done