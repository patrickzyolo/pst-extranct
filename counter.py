#!/usr/bin/python
import sys
import os
import time

while True:
	sys.stdout.write("data: " + str(len(os.listdir("./data/"))))
#	sys.stdout.write(" size: " + str(os.path.getsize("./data/")))
	sys.stdout.write(" Attachments: " + str(len(os.listdir("./Attachments/"))) + "\r")
#       sys.stdout.write(" size: " + str(os.path.getsize("./Attachments/")) + "\r")
	sys.stdout.flush()

	time.sleep(20)
