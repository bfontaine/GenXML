#! /bin/bash
set -e

EXE=./gedcom2xml
XML=xml
HTML=html

mkdir -p $XML
mkdir -p $HTML

__test_ged2xml() {
  local ged=$1
  local filename=${ged##*/}
  local basename=${filename%%.*}
  local xml=$XML/${basename}.xml
  local html=$HTML/${basename}.html
  local ok=" [OK]"

  echo "* $filename"
  echo -n "  - XML"
  $EXE $ged $xml && echo "$ok"
  echo -n "  - DTD validation"
  xmllint --noout --dtdvalid gedcom.dtd $xml && echo "$ok"
  echo -n "  - XSD validation "
  xmllint --noout --schema gedcom.xsd $xml
  echo -n "  - XSLT -> HTML"
  xsltproc gedcom.xsl $xml >$html && echo "$ok"
}

for ged in sources/*.ged; do
  __test_ged2xml $ged
done
