#!/usr/bin/python				# This is server.py file

import socket					# Import socket module
import threading				# Import threading
import datetime
import time
import SimpleHTTPServer
import SocketServer
import select

def echo(clientSocket):
	clientSocket.setblocking(0)		#make socket connection non-blocking
	while(1):
		ready = select.select([clientSocket], [], [], 2)	#wait for 3 seconds or time till heartbeat is received whichever is less to receive the heartbeat of the server
		if ready[0]:										#if the heartbeat is received
			data = clientSocket.recv(1)
			if data == 'p':
				print 'ping from client, sending echo'
				clientSocket.send('e')
			else :
				print 'unknown message from client'
		else:
			print 'No ping from client even after 2 seconds of wait'
		time.sleep(1)

#create a socket object and bind to the port and wait for client
serverSocket = socket.socket()		
host = socket.gethostname()
port = 8199
serverSocket.bind((host, port))		
serverSocket.listen(5)				

while 1:
	clientSocket, addr = serverSocket.accept()		# Establish connection with client.
	threading.Timer(1, echo, [clientSocket]).start()
	time.sleep(2)