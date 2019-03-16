package me.jordan.piclient.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import oshi.SystemInfo;

public class Client {
	private Socket connection;
	private String message;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	public boolean connect = false;

	public Client() {
	}

	public void start() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				connectToServer();
			}
		});
		t.start();
	}

	public void connectToServer() {
		System.out.println("ATTEMPTING SERVER CONNECTION");
		try {
			this.connection = new Socket("10.24.69.172", 8222);
			this.setupStreams();
			this.whileChatting();
			this.connect = true;
		} catch (UnknownHostException var8) {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException var7) {
				var7.printStackTrace();
			}

			this.connectToServer();
		} catch (IOException var9) {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException var6) {
				var6.printStackTrace();
			}

			this.connectToServer();
		}

	}

	public void setupStreams() throws IOException {
		this.output = new ObjectOutputStream(this.connection.getOutputStream());
		this.output.flush();
		this.input = new ObjectInputStream(this.connection.getInputStream());
		System.out.println("CONNECTED TO SERVER");
		sendHostMessage("ipv4:" + getIPv4());
		new Thread(() -> {
			while (true) {
				try {
					try {
						SystemInfo si = new SystemInfo();
						long avail = si.getHardware().getMemory().getAvailable();
						double mem = (double) avail / si.getHardware().getMemory().getTotal();
						double freeMemPercent = 100 - (mem * 100);
						sendHostMessage("cpuusage:" + executeCommand("mpstat"));
						sendHostMessage("usage:" + freeMemPercent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void whileChatting() throws IOException {
		do {
			try {
				this.message = (String) this.input.readObject();
				System.out.println(this.message);
			} catch (ClassNotFoundException var9) {

			}
		} while (!this.message.equals("END"));

	}

	private String getIPv4() {
		String ip = null;
		try {
		    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		    while (interfaces.hasMoreElements()) {
		        NetworkInterface iface = interfaces.nextElement();
		        // filters out 127.0.0.1 and inactive interfaces
		        if (iface.isLoopback() || !iface.isUp())
		            continue;

		        Enumeration<InetAddress> addresses = iface.getInetAddresses();
		        while(addresses.hasMoreElements()) {
		            InetAddress addr = addresses.nextElement();

		            // *EDIT*
		            if (addr instanceof Inet6Address) continue;

		            ip = addr.getHostAddress();
		            System.out.println(iface.getDisplayName() + " " + ip);
		        }
		    }
		} catch (SocketException e) {
		    throw new RuntimeException(e);
		}
		return ip;
	}
	
	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {

			p = Runtime.getRuntime().exec(command);

			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public void sendHostMessage(String message) {
		try {
			this.output.writeObject(message);
			this.output.flush();
		} catch (IOException var3) {
			
		}

	}
}
