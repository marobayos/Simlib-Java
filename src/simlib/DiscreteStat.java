package simlib;

import java.io.FileWriter;
import java.io.IOException;

public class DiscreteStat {
	private float sum;
	private int numObs;
	private float max;
	private float min;
	private String name;

	public DiscreteStat(String name) {
		sum = 0;
		numObs = 0;
		max = (float)-3.4E+38;
		min = (float)3.4E+38;
	}

	public float getDiscreteSum() {
		return sum;
	}

	public int getDiscreteObs() {
		return numObs;
	}

	public float getDiscreteMax() {
		return max;
	}

	public float getDiscreteMin() {
		return min;
	}

	public void recordDiscrete(float value) {
		sum += value;
		numObs += 1;
		if (value > max)
			max = value;
		if (value < min)
			min = value;
	}
	
	public float getDiscreteAverage() {
		return sum/numObs;
	}

	public void report(FileWriter out) throws IOException {
		out.write("REPORT DE "+name+" :\n");
		out.write("Promedio:\t"+getDiscreteAverage()+"\n");
		out.write("Valor mínimo: "+this.min+"\n");
		out.write("Valor máximo: "+this.max+"\n");
	}
}