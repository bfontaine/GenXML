#! /bin/bash

EXE=./gedcom2xml
TESTS=_tests

mkdir -p $TESTS

__test_ged2xml() {
  local ged=$1
  local filename=${ged##*/}
  local base=$TESTS/${filename%%.*}
  local xml=${base}.xml

  echo "$filename -> $xml"
  $EXE $ged $xml
}

for ged in sources/*.ged; do
  __test_ged2xml $ged
done
