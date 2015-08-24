#!/bin/sh
# java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON $1
java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost $1
