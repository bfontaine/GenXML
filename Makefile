
PROJECT=gedcom2xml
VERSION=1.0

JAR=$(PROJECT).jar
EXE=$(PROJECT)

SRC=parser
SRCS=$(wildcard $(SRC)/src/main/scala/*.scala)

SCALA_VERSION=2.10
SRC_JAR=$(SRC)/target/scala-$(SCALA_VERSION)/$(PROJECT)_$(SCALA_VERSION)-$(VERSION)-one-jar.jar

CP=cp
RM=rm -f
SH=$(shell which sh)

.PHONY: all clean mrproper

all: $(JAR) $(EXE)


$(JAR): $(SRC_JAR)
	$(CP) $< $@

$(SRC_JAR): $(SRCS)
	cd $(SRC) && sbt one-jar

$(EXE):
	$(RM) $@
	echo "#! $(SH)" >> $@
	echo "java -jar $(JAR) \$$1 \$$2" >> $@
	chmod u+x $@

clean:
	$(RM) $(EXE) $(JAR)

mrproper: clean
	$(RM) $(SRC_JAR)
