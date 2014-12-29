package paxos_simulation;

public class ExponentialDelay implements DelayGenerator {
	private double lambda;
	public ExponentialDelay(double lambda) {
		this.lambda = lambda;
	}
	
	@Override
	public long generateDelay(double mean) {
		return (long) (mean*Math.log(Math.random())/(-lambda));
	}
	
	public static void main(String[] args) {
		ExponentialDelay gen = new ExponentialDelay(1.5);
		for (int i = 0; i < 10; i++) {
			System.out.println(gen.generateDelay(100));
		}
	}

}
