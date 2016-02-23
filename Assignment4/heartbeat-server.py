#!/usr/bin/python				# This is server.py file

import socket					# Import socket module
import threading				# Import threading
import datetime
import time

#sends heartbeat of the server after every 1 second.
def heartbeat(clientSocket):
	clientSocket.send('h')		#heartbeat
	threading.Timer(1, heartbeat, [clientSocket]).start()

#create a socket object and bind to the port and wait for client
serverSocket = socket.socket()		
host = socket.gethostname()
port = 8188
# PORT = 8095
serverSocket.bind((host, port))		
serverSocket.listen(5)				

while 1:
	clientSocket, addr = serverSocket.accept()		# Establish connection with client.
	threading.Timer(1, heartbeat, [clientSocket]).start()		#create a thread for the client that send the heartbeat of the server after every 1 second
	time.sleep(2)