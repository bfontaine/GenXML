
PROJECT=gedcom2xml
VERSION=1.0

JAR=$(PROJECT).jar
EXE=$(PROJECT)

TESTS=tests.sh

TMP=/tmp
SUBMISSION_NAME=fontaine-galichet-gedcom2xml
SUBMISSION=$(TMP)/$(SUBMISSION_NAME)
TARBALL=$(TMP)/$(SUBMISSION_NAME).tgz

SRC=parser
SRCS=$(wildcard $(SRC)/src/main/scala/*.scala)

SCALA_VERSION=2.10
SRC_JAR=$(SRC)/target/scala-$(SCALA_VERSION)/$(PROJECT)_$(SCALA_VERSION)-$(VERSION)-one-jar.jar

SHELL := /bin/bash

JAR_DIR?=$(shell pwd)

CP=cp
RM=rm -f
RM_R=rm -rf
SH=$(shell which sh)

.PHONY: all clean mrproper tests tarball

all: $(JAR) $(EXE)


$(JAR): $(SRC_JAR)
	$(CP) $< $@

$(SRC_JAR): $(SRCS)
	cd $(SRC) && sbt one-jar

$(EXE):
	$(RM) $@
	echo "#! $(SH)" > $@
	echo "java -jar $(JAR_DIR)/$(JAR) \$$*" >> $@
	chmod u+x $@

tests: $(EXE) $(TESTS)
	@./$(TESTS)

tarball: $(SRC_JAR) tests
	make -B JAR_DIR=. $(EXE) && \
	$(RM) $(TARBALL) && \
	$(RM_R) $(SUBMISSION) && \
	$(CP) -r . $(SUBMISSION) && \
	pushd $(SUBMISSION) && \
	$(RM_R) .git .DS_Store .*.swp .*.swo *~ README.md && \
	$(RM_R) $(SRC)/target $(SRC)/project/project $(SRC)/project/target && \
	pandoc docs/rapport.md -o rapport.pdf && \
	$(RM_R) docs && \
	pushd $(TMP) && \
	tar czvf $(SUBMISSION_NAME).tgz $(SUBMISSION_NAME) && \
	popd && popd

clean:
	$(RM) $(EXE) $(JAR)
	$(RM_R) $(SUBMISSION)

mrproper: clean
	$(RM) $(SRC_JAR) $(TARBALL)
