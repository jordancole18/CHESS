import socket
import threading
from threading import *

HOST = '127.0.0.1'
PORT = 2184
RUNNING = True
CONNECTIONS = []

class ServerClientConnection(thread.Threading):
    def __init__(self, conn, addr):
        super(ServerClientConnection, self).__init__();
        self.conn = conn
        self.addr = addr
    def run(self):
        with self.conn:
            print('Connected by', self.addr)
            while RUNNING:
                try:
                    data = self.conn.recv(1024).decode("utf-8")
                    if not data:
                        break
                    info = data.split(":")
                    print(data)
                except:
                    print(self.addr, ">> forcefully disconnected.")
                    CONNECTIONS.remove(self)
                    break;
    def sendData(self, data):
        self.conn.sendall(data)


class Server:

    def __init__(self):
        print("Starting Server...")
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s = s
        self.s.bind((HOST, PORT))
        self.s.listen()
        while RUNNING:
            conn, addr = s.accept()
            serverClientConn = ServerClientConnection(conn, addr)
            CONNECTIONS.append(serverClientConn)
            serverClientConn.start()

    def getConnections(self):
        return self.connections

    def stop():
        self.s.close()
        RUNNING = False

    def sendMessage(self, serverClientConn, data):
        serverClientConn.sendData(data)
