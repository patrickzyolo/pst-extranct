#!/bin/sh
javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java
javac -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailPost.java
