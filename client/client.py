import socket
import threading
from threading import *

HOST = '127.0.0.1'
PORT = 2186
RUNNING = True

class ClientConnection (threading.Thread):
    def __init__(self, socket):
        super(ClientConnection, self).__init__()
        self.socket = socket

    def run(self):
        self.socket.connect((HOST, PORT))
        self.socket.sendall(b'01:CONNECTED')
        while RUNNING:
            data = self.socket.recv(1024)
            if not data:
                break;
            print('Client Received', repr(data))
    def sendData(self, data):
        self.socket.sendall(data)




class Client:
    def __init__(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s = s
        clientConn = ClientConnection(s)
        self.clientConn = clientConn
        self.clientConn.start()
    def sendData(self, data):
        self.clientConn.sendData(data)
    def disconnect(self):
        self.s.close()

c = Client()
