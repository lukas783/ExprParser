################ MAKEFILE TEMPLATE ################

# Author : Lucas Carpenter

# Usage : make target1

# What compiler are we using? (gcc, g++, nvcc, etc)
LINK = javac

# Name of our binary executable
#OUT_FILE = edge

# Any weird flags ( -O2/-O3/-Wno-deprecated-gpu-targets/-fopenmp/etc)
FLAGS = -d ./ -cp src
DOCFLAGS = -d ./doc/ -author
all: Parser

Parser: src/Parser.java src/Evaluator.java
	$(LINK) $(FLAGS) $^

docs: src/Parser.java src/Evaluator.java
	javadoc $(DOCFLAGS) $^
clean: 
	rm -f *.class *.html *.css *.js package-list *~ core
