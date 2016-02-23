#!/usr/bin/python           # This is client.py file

import socket               # Import socket module
import os
import time
import threading
import select
import sys

# it listens to the heartbeat of the server. Socket receive is non-blocking so, 
# it waits for 3 seconds to receive the heartbeat of the server, if there is no heartbeat
# then, server is down as the server sends the heartbeat after every 1 second
def heartbeatHear(socketClient):
	socketClient.setblocking(0)		#make socket receive non-blocking
	while(1):
		ready = select.select([socketClient], [], [], 5)	#wait for 5 seconds or time till heartbeat is received whichever is less to receive the heartbeat of the server
		if ready[0]:										#if the heartbeat is received
			data = socketClient.recv(1)
			if data == 'h':
				print 'heartbeat up'
				heartbeatTime = time.time()
			else:
				print 'unknown message from server'
			# print "Heartbeat is up and running"
		else:
			print "No heartbeat from server"
			sys.exit()
		time.sleep(1)

#create a socket object
socketClient = socket.socket()
host = socket.gethostname()
# host = "54.213.202.99"
port = 8188

#connect to the server
socketClient.connect((host, port))
threading.Timer(1, heartbeatHear, [socketClient]).start()	#create a threads that listens to the heartbeat of the server
