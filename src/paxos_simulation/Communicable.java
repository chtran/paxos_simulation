package paxos_simulation;

public interface Communicable {
	public void processResponse(String response);
	public String getResponse(String message);
}
