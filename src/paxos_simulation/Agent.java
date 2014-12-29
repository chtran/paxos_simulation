package paxos_simulation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {
	private int id;
	private String hostname;
	private int port;
	private ReceiveThread receiveThread;
	private Map<Integer, Integer> portMap; // from id to port
	private ClassicPaxosResponder responder;
	private DelayGenerator delayGenerator;
	private int currentRound=1;
	private double x;
	private double y;
	private Map<Integer, Double> meanDelay;
	
	public Agent(int id, double x, double y, String hostname, int port) throws IOException {
		this.id = id;
		this.x = x;
		this.y = y;
		this.hostname = hostname;
		this.port = port;
		this.responder = new ClassicPaxosResponder(this);

		this.receiveThread = new ReceiveThread();
		this.receiveThread.start();
		this.portMap = new HashMap<Integer, Integer>();
		this.delayGenerator = new GaussianDelay(10);
		this.meanDelay = new HashMap<Integer, Double>();
	}
	
	
	
	public void sendPropose(int value, int agentID, AtomicInteger counter, long start) {
		String message = responder.createProposeMessage(currentRound, value);
		if (agentID == this.id) {
			responder.onPropose(message); // Send a message to itself
			return;
		}
		SendThread st = new SendThread(agentID, message, counter, start);
		st.start();
	}
	
	public String sendMessage(int to_id, String message) {
		try {
			int port = portMap.get(to_id);
			Client client = new Client(hostname, port, responder);
			client.connect();
			long delay = delayGenerator.generateDelay(meanDelay.get(to_id));
			return client.sendMessage(delay, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addConnection(Agent agent) {
		this.portMap.put(agent.id, agent.port);
		double distance = this.getDistance(agent);
		meanDelay.put(agent.id, distance);
	}
	
	public double getDistance(Agent to) {
		return Math.sqrt(Math.pow(this.x - to.x, 2) + Math.pow(this.y - to.y, 2));
	}
	

	public int id() {
		return this.id;
	}
	
	public int port() {
		return this.port;
	}
	
	public String hostname() {
		return this.hostname;
	}
	

	private class SendThread extends Thread {
		private int agentID;
		private String message;
		private AtomicInteger counter;
		private long start;
		
		public SendThread(int agentID, String message, AtomicInteger counter, long start) {
			this.agentID = agentID;
			this.message = message;
			this.counter = counter;
			this.start = start;
		}
		public void run() {
			String response = sendMessage(agentID, message);
			String[] splitted = response.split("\t");
			if (splitted[1].equals("propose_accept")) {
				int val = counter.getAndDecrement();
				if (val == 1) {
					long elapsed = System.currentTimeMillis() - start;
					System.out.printf("Proposal succeeded in %dms\n", elapsed);
					currentRound++;
				}
			}
		}
	}
	
	private class ReceiveThread extends Thread {
		private Server server;

		public void run() {
			this.server = new Server(id, port, responder);
			try {
				this.server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
