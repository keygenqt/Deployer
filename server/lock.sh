#!/bin/bash

if test -f "./.lock"; then
  rm "./.lock"
  exit 0
else
  echo "lock" >"./.lock"
fi