package paxos_simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentPool {
	private Map<Integer, Agent> agentMap;
	private int lastID;
	public AgentPool() {
		this.agentMap = new HashMap<Integer, Agent>();
	}
	
	// Leader is the highest ID
	public Agent getLeader() {
		int maxID = -1;
		for (int id: agentMap.keySet()) {
			if (id > maxID) maxID = id;
		}
		return agentMap.get(maxID);
	}
	
	public void addAgent(double x, double y) {
		int id = lastID++;
		int port = 2000+id;
		try {
			Agent agent = new Agent(id, x, y, "localhost", port);
			addConnection(agent);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void addConnection(Agent agent) {
		for (int id: agentMap.keySet()) {
			Agent oldAgent = agentMap.get(id);
			oldAgent.addConnection(agent);
			agent.addConnection(oldAgent);
		}
		agentMap.put(agent.id(), agent);

	}
	
	private List<Agent> getClosestAgents(Agent from, int k) {
		AgentComparator ac = new AgentComparator(from);
		@SuppressWarnings("unchecked")
		PriorityQueue<Agent> pq = new PriorityQueue<Agent>(k, ac);
		for (int agentID: agentMap.keySet()) {
			if (agentID != from.id()) {
				pq.add(agentMap.get(agentID));
			}
			if (pq.size() > k) pq.remove();
		}
		List<Agent> toReturn = new ArrayList<Agent>();
		toReturn.addAll(pq);
		return toReturn;
	}
	
	
	// Fast Paxos
	public void fastPropose(int value) {
		System.out.println("\nStarting Fast Paxos");

		int N = agentMap.size();
		int required = (int) (N - Math.floor(N/4));
		
		AtomicInteger counter = new AtomicInteger(required);
		Agent leader = getLeader();
		long start = System.currentTimeMillis(); 
		for (int agentID: agentMap.keySet()) {
			leader.sendPropose(value, agentID, counter, start);
		}
		while(counter.get()!=0){}
	}

	// Classic Paxos
	public void classicPropose(int value) {
		System.out.println("\nStarting Classic Paxos");
		long toLeader = 200;
		
		int N = agentMap.size();
		int required = (int) Math.ceil(N/2);
		
		AtomicInteger counter = new AtomicInteger(required);
		Agent leader = getLeader();
		long start = System.currentTimeMillis(); 
		try {
			System.out.printf("Sending to leader in %d \n", toLeader);
			Thread.sleep(toLeader);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int agentID: agentMap.keySet()) {
			leader.sendPropose(value, agentID, counter, start);
		}
		while(counter.get()!=0){}

	}
	
	// Fixed Paxos
	public void fixedPropose(int value) {
		System.out.println("\nStarting Fixed Paxos");

		int N = agentMap.size();
		int required = (int) Math.ceil(N/2);
		
		AtomicInteger counter = new AtomicInteger(required);
		Agent leader = getLeader();
		List<Agent> toSend = getClosestAgents(leader, required);
		long start = System.currentTimeMillis();
		for (Agent a: toSend) {
			leader.sendPropose(value, a.id(), counter, start);
		}
		while(counter.get()!=0){}
	}
	
	@SuppressWarnings("rawtypes")
	private class AgentComparator implements Comparator {
		private Agent source;
		public AgentComparator(Agent source) {
			this.source = source;
		}
		public int compare(Object o1, Object o2) {
			return (int) (source.getDistance((Agent) o2) - source.getDistance((Agent) o1));
		}
		
	}
}
