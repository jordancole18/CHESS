package net.jordan.piserver.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map.Entry;

public class ServerConnection extends Thread {

	public Socket socket;
	public ObjectOutputStream output;
	public ObjectInputStream input;
	public String ipv4;
	public HashMap<Long, Double> cpuAverage = new HashMap<Long, Double>();
	public HashMap<Long, Double> memAverage = new HashMap<Long, Double>();
	public double lastMem = -1;
	
	public ServerConnection(Socket connected) {
		this.socket = connected;
		ipv4 = "";
	}

	public synchronized HashMap<Long, Double> getCPUAverage() {
		return cpuAverage;
	}
	
	public synchronized HashMap<Long, Double> getMemAverage(){
		return memAverage;
	}
	
	public void run() {
		String message = "";
		if (this.output == null || this.input == null) {
			try {
				this.output = new ObjectOutputStream(this.socket.getOutputStream());
				this.output.flush();
				this.input = new ObjectInputStream(this.socket.getInputStream());
			} catch (IOException var10) {

			}
		}

		while (true) {

			do {
				try {
					if (this.input != null) {
						message = (String) this.input.readObject();
						//System.out.println("From Client: " + message);
						// FileManager fm = FileManager.getInstance();

						if(message.startsWith("ipv4:")) {
							this.ipv4 = message.replace("ipv4:", "");
							System.out.println("Set server ipv4 to: " + ipv4);
						}
						
						
						if(message.startsWith("cpuusage:")) {
							
							String s = message.replace("cpuusage:", "");
							String[] data = s.split(" +");
							double idle = Double.parseDouble(data[27]);
							//double usage = 100 - idle;
							//System.out.print("CPU Usage: " + usage + "%");
							cpuAverage.put(System.currentTimeMillis(), idle);
							
						}
						
						if (message.startsWith("usage:")) {
							String s = message.replace("usage:", "");
							double mem = Double.parseDouble(s);
							memAverage.put(System.currentTimeMillis(), mem);
							lastMem = mem;
						}

					}
				} catch (ClassNotFoundException var11) {

				} catch (IOException var12) {

				}
			} while (!message.equalsIgnoreCase("stopserverend"));

		}
	}

	public double getCPULastMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : cpuAverage.entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 60000) {
					avg += entry.getValue();
				}
			}	
			if(cpuAverage.size() > 0) {
				return avg/cpuAverage.size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}
	
	public double getCPULastFiveMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : cpuAverage.entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 300000) {
					avg += entry.getValue();
				}
			}	
			if(cpuAverage.size() > 0) {
				return avg/cpuAverage.size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}
	
	public double getCPULastTenMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : cpuAverage.entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 600000) {
					avg += entry.getValue();
				}
			}	
			if(cpuAverage.size() > 0) {
				return avg/cpuAverage.size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}
	
	public double getMemLastMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : getMemAverage().entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 60000) {
					avg += entry.getValue();
				}
			}	
			if(getMemAverage().size() > 0) {
				return avg/getMemAverage().size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}
	
	public double getMemLastFiveMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : getMemAverage().entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 300000) {
					avg += entry.getValue();
				}
			}	
			if(getMemAverage().size() > 0) {
				return avg/getMemAverage().size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}
	
	public double getMemLastTenMinuteAvg(){
		try {
			long current = System.currentTimeMillis();
			double avg = 0;
			for(Entry<Long, Double> entry : getMemAverage().entrySet()) {
				long diff = current - entry.getKey();
				if(diff <= 600000) {
					avg += entry.getValue();
				}
			}	
			if(getMemAverage().size() > 0) {
				return avg/getMemAverage().size();
			}else {
				return -1;
			}
		}catch(ConcurrentModificationException e) {
			return -1;
		}
	}

	public void sendMessage(String message) {
		if (socket.isConnected() == false) {
			try {
				socket.close();
			} catch (IOException e) {
			}
			return;
		}
		try {
			this.output.writeObject(message);
			this.output.flush();
		} catch (IOException var3) {
		}

	}

}
