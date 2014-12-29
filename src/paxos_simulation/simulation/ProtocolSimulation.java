package paxos_simulation.simulation;

import paxos_simulation.AgentPool;


public class ProtocolSimulation {
	public static void main(String[] args) {
		AgentPool agentPool = new AgentPool();
		agentPool.addAgent(0, 0);
		agentPool.addAgent(100, 0);
		agentPool.addAgent(0, 100);
		agentPool.addAgent(0, 200);
		agentPool.addAgent(0, 300);
		int nIters = 50000;
		
		for (int i = 0; i < nIters; i++)
			agentPool.classicPropose(3);
		
	}
}
