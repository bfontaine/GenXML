#! /bin/bash
set -e

EXE=./gedcom2xml
TESTS=_tests

mkdir -p $TESTS

__test_ged2xml() {
  local ged=$1
  local filename=${ged##*/}
  local base=$TESTS/${filename%%.*}
  local xml=${base}.xml
  local ok=" [OK]"

  echo "* $filename"
  echo -n "  - XML"
  $EXE $ged $xml && echo "$ok"
  echo -n "  - DTD validation"
  xmllint --noout --dtdvalid gedcom.dtd $xml && echo "$ok"
  echo "  - XSD validation"
  xmllint --noout --schema gedcom.xsd $xml && echo "$ok"
  #echo "  - XSLT -> HTML"
}

for ged in sources/*.ged; do
  __test_ged2xml $ged
done
