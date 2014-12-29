package paxos_simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket serverSocket;
	private int port;
	private Communicable responder;
	private int id;
	
	public Server(int id, int port, Communicable responder) {
		this.port = port;
		this.responder = responder;
		this.id = id;
	}
	
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		while (true) {
			Socket client = serverSocket.accept();
			InputStream is = client.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            //System.out.println(id + " received "+message);
            String response = responder.getResponse(message);
            OutputStream os = client.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            //System.out.println("Responding " + response);

            bw.write(response+"\n");
            bw.flush();
            client.close();
		}
		
	}
}