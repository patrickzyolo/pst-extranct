javac:
	javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java

sample:
	mkdir -pv data
	mkdir -pv Attachments

	java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON c.pozzi.pst

all:
	find /Users/hacker/Desktop/Hacked\ Team/ -name "*.pst" -exec echo {} \; -exec java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON {} \;

clean:
	rm -f "PstMailJSON.class"
	rm -rf data
	rm -rf Attachments

	mkdir data
	mkdir Attachments

default: javac
