package paxos_simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private String hostname;
	private int port;
	Socket clientSocket;
	
	public Client(String hostname, int port, Communicable responder) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void connect() throws UnknownHostException, IOException {
		clientSocket = new Socket(hostname, port);
	}
	
	public String sendMessage(long delay, String message) throws IOException {
		try {
			System.out.printf("Delaying %d \n", delay);
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sendMessage(message);
	}
	
	public String sendMessage(String message) throws IOException {
		OutputStream os = clientSocket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(message + "\n");
		bw.flush();
		
		InputStream is = clientSocket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		String response = br.readLine();

		
		clientSocket.close();
		return response;
	}
}
