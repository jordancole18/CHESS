package net.jordan.piserver;

import java.io.File;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.jordan.piserver.net.Server;
import net.jordan.piserver.net.ServerConnection;

public class PIServer {

	public static void main(String[] args) {
		new PIServer();
	}

	private Server server;

	public PIServer() {
		server = new Server();
		server.acceptConnections();
		new Thread(() -> {

			while (true) {
				int node = 1;
				JsonObject main = new JsonObject();
				String forward = "";
				double cpuAvg = -1;
				double memAvg = -1;
				System.out.println("");
				for (ServerConnection sc : server.getServerConnections()) {
					System.out.println(node + ". " + sc.lastMem + " - " + sc.ipv4);
					if (memAvg == -1 || cpuAvg == -1) {
						cpuAvg = sc.lastMem;
						memAvg = sc.lastMem;
						forward = sc.ipv4;
					} else if (sc.lastMem < memAvg) {
						cpuAvg = sc.lastMem;
						memAvg = sc.lastMem;
						forward = sc.ipv4;
					}
					JsonObject jo = new JsonObject();
					jo.addProperty("ipv4", sc.ipv4);
//					JsonObject cpu = new JsonObject();
//					cpu.addProperty("1-minute", sc.getCPULastMinuteAvg());
//					cpu.addProperty("5-minute", sc.getCPULastFiveMinuteAvg());
//					cpu.addProperty("10-minute", sc.getCPULastTenMinuteAvg());
					JsonObject mem = new JsonObject();
					mem.addProperty("1-minute", sc.getMemLastMinuteAvg());
					mem.addProperty("5-minute", sc.getMemLastFiveMinuteAvg());
					mem.addProperty("10-minute", sc.getMemLastTenMinuteAvg());
					double realmem = round(sc.lastMem, 2);
					jo.addProperty("real-mem", realmem);
					jo.add("mem", mem);
					main.add("node" + node + "", jo);
					node++;
				}
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String prettyJson = gson.toJson(main);
				String filePath = "C:/xampp/htdocs/api/serverinfo.txt";
				String filePath2 = "C:/xampp/htdocs/forward.txt";

				File file = new File(filePath);
				File file2 = new File(filePath2);
				if (!file.exists())
					file.mkdirs();
				if (!file2.exists()) {
					file2.mkdirs();
				}
				// System.out.println("Redirect: " + forward);
				try {
					PrintWriter writer = new PrintWriter(file);
					writer.write(prettyJson);
					writer.close();
					PrintWriter writer2 = new PrintWriter(file2);
					writer2.write(forward);
					writer2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println(prettyJson);
			}

		}).start();
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

}
