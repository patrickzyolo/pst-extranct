#!/bin/sh
javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java
javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailPlain.java

# java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON c.pozzi.pst
# java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPlain c.pozzi.pst

# find /Users/hacker/Desktop/Hacked\ Team/ -name "*.pst" -exec echo {} \; -exec java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON {} \;
