package paxos_simulation;

public class ClassicPaxosResponder implements Communicable {
	public static int NONE = -1;
	private Agent agent;
	private int highestRound;
	private int currentValue;
	
	public ClassicPaxosResponder(Agent agent) {
		this.agent = agent;		
		this.highestRound = NONE;
		this.currentValue = NONE;
	}
	
	public String createProposeMessage(int roundNumber, int value) {
		if (highestRound > roundNumber) {
			return null;
		}
		highestRound = roundNumber;
		currentValue = value;
		return String.format(
				"%d\t%s\t%d\t%d", 
				agent.id(), "propose", roundNumber, value);
	}
	
	private String createProposeDeclineResponse() {
		return String.format(
				"%d\t%s",
				agent.id(), "propose_decline");
	}
	
	private String createProposeAcceptResponse(int roundNumber, int value) {
		return String.format(
				"%d\t%s\t%d\t%d",
				agent.id(), "propose_accept", roundNumber, value);
	}
	
	private String getMessageType(String message) {
		String[] splitted = message.split("\t");
		return splitted[1];
	}
	
	/*
	 * When agent sent a message and got a response
	 */
	public void processResponse(String response) {
		System.out.println(response);
	}
	
	/*
	 * When someone sent agent a message
	 */
	public String getResponse(String message) {
		String type = getMessageType(message);
		switch (type) {
		case "propose":
			return onPropose(message);
		}
		return null;
	}
	
	
	public String onPropose(String message) {
		String[] splitted = message.split("\t");
		int roundNumber = Integer.parseInt(splitted[2]);
		int value = Integer.parseInt(splitted[3]);
		
		if (roundNumber > highestRound || (roundNumber == highestRound && value == currentValue)) {
			return createProposeAcceptResponse(roundNumber, value);
		} else {
			return createProposeDeclineResponse();
		}
	}
}
