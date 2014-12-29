package paxos_simulation;

import java.util.Random;

public class GaussianDelay implements DelayGenerator {
	private double std;
	public GaussianDelay(double std) {
		this.std = std;
	}
	
	@Override
	public long generateDelay(double mean) {
		Random rnd = new Random();
		return (long) (mean + std*rnd.nextGaussian());
	}
}
