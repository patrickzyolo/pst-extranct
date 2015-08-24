new:
	rm -f "PstMailJSON.class"
	rm -f "PstMailPost.class"
	rm -rf data
	rm -rf Attachments

	mkdir data
	mkdir Attachments
	javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java
	javac -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost.java

clean:
	rm -f "PstMailJSON.class"
	rm -f "PstMailPost.class"
	rm -f "PstMailJSON_auto.class"
	rm -f "PstMailPost_auto.class"
	rm -rf data
	rm -rf Attachments

javac:
	javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java
	javac -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost.java

javac-auto:
	javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON_auto.java
	javac -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost_auto.java

curl-delete:
	curl -XDELETE http://debian.local:9200/hacking-team/mail/

all:
	find /home/patrick/Hacking-Team/mail -name "*.pst" -exec echo {} \; -exec java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost {} \;

test:
	java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost c.pozzi.pst

 default: javac
