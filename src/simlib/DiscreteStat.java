package simlib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DiscreteStat {
	private float sum;
	private int numObs;
	private Float max;
	private Float min;
	private Float prevValue;
	private String name;

	public DiscreteStat(Float value, String name){
		sum = 0;
		numObs = 0;
		max = min = prevValue = value;
		this.name = name;
	}

	public DiscreteStat(String name) {
		this(null, name);
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
		if (max == null){
			max = value;
			min = value;
			prevValue = value;
		}
		if (value > max)
			max = value;
		if (value < min)
			min = value;
	}

	public float getDiscreteAverage() {
		return sum/numObs;
	}

	public void report(BufferedWriter out) throws IOException {
		out.write("REPORTE DEL "+name+":\n");
		out.write("\tPromedio:\t"+getDiscreteAverage()+"\n");
		out.write("\tValor mínimo: "+this.min+"\n");
		out.write("\tValor máximo: "+this.max+"\n\n");
	}
}