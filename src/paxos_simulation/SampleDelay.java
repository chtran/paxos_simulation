package paxos_simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SampleDelay implements DelayGenerator {
	private ArrayList<Integer> delays;
	public SampleDelay(String filename) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(filename));
			delays = new ArrayList<Integer>();
			while (scanner.hasNextInt()) {
				delays.add(scanner.nextInt());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public long generateDelay(double mean) {
		int i = (int) Math.floor(Math.random() * this.delays.size());
		return delays.get(i);
	}


}
