package net.jordan.piserver.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	private ServerSocket serverSocket;
	private Socket acceptingSocket;
	private List<ServerConnection> serverConnections;

	public Server() {
		this.serverConnections = new ArrayList<ServerConnection>();
	}

	public void acceptConnections() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(8222, 10, InetAddress.getByName("10.24.69.172"));
					System.out.println(serverSocket.getInetAddress().getHostAddress());
					while (true) {
						try {
							acceptingSocket = serverSocket.accept();
						} catch (IOException e) {
							System.out.println("I/O error: " + e);
						}
						ServerConnection thread = new ServerConnection(acceptingSocket);
						serverConnections.add(thread);
						thread.start();
						System.out.println("Server Added!");
					}
				} catch (IOException localIOException1) {
				}
			}
		});
		t.start();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public synchronized List<ServerConnection> getServerConnections() {
		return serverConnections;
	}

}
