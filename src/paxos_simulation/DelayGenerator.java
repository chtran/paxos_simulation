package paxos_simulation;

public interface DelayGenerator {
	public long generateDelay(double mean);
}
