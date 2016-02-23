#!/usr/bin/python           # This is client.py file

import socket               # Import socket module
import os
import time
import threading
import select
import sys
import os

# it listens to the heartbeat of the server. Socket receive is non-blocking so, 
# it waits for 3 seconds to receive the heartbeat of the server, if there is no heartbeat
# then, server is down as the server sends the heartbeat after every 1 second
def echoHear(socketClient):
	socketClient.setblocking(0)		#make socket receive non-blocking
	while(1):
		ready = select.select([socketClient], [], [], 2)	#wait for 3 seconds or time till heartbeat is received whichever is less to receive the heartbeat of the server
		if ready[0]:										#if the heartbeat is received
			data = socketClient.recv(1)
			if data == 'e':
				print 'ping up'
			else:
				print 'unknown message from server'
			# print "Heartbeat is up and running"
		else:
			print "No reply from server"
			print "fault detected"
			os._exit(1)
		time.sleep(1)

#it pings the server after every second with deadline 2 seconds to receive a packet
#if the packet is received, server is up else it is down
def pingServer(socketClient):
	socketClient.send('p')			#send ping
	threading.Timer(1, pingServer, [socketClient]).start()

#create a socket object
socketClient = socket.socket()
host = socket.gethostname()
# host = "54.213.202.99"
port = 8199

#connect to the server
socketClient.connect((host, port))
threading.Timer(1, echoHear, [socketClient]).start()	#create a threads that listens to the heartbeat of the server
threading.Timer(1, pingServer, [socketClient]).start()			#create a thread that pings server after every 1 second to check the server
