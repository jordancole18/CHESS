package me.jordan.piclient;

import java.io.IOException;

import me.jordan.piclient.net.Client;

public class PIClient {

	public static void main(String[] args) throws IOException {
		new PIClient();
	}
	
	private Client client;

	public PIClient() {
		client = new Client();
		client.start();

	}

}
