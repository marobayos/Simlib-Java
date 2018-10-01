package simlib.elements;

import simlib.io.SimWriter;
import java.io.IOException;

public class DiscreteStat {
	private float sum;
	private int numObs;
	private Float max;
	private Float min;
	private Float lastUpdate;
	private String name;

	public DiscreteStat(Float value, String name){
		sum = 0;
		numObs = 0;
		max = min = lastUpdate = value;
		this.name = name;
	}

	public DiscreteStat(String name) {
		this(null, name);
	}

	public float getSum() {
		return sum;
	}

	public int getRequest() {
		return numObs;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public void record(float value) {
		sum += value;
		numObs += 1;
		if (max == null){
			max = value;
			min = value;
			lastUpdate = value;
		}
		if (value > max)
			max = value;
		if (value < min)
			min = value;
	}

	public float getAverage() {
		return sum/numObs;
	}

	public void report(SimWriter out) throws IOException {
		out.write("************************************************************\n");
		out.write(this.completeLine("*  DISCRETE STATISTIC "+name));
		out.write("************************************************************\n");
		out.write(completeLine(this.completeHalfLine("*  Min = "+min)+"  Max = "+max));
		out.write(this.completeLine("*  Records = "+numObs));
		out.write(this.completeLine("*  Average = "+this.getAverage()));
		out.write("************************************************************\n\n");
	}

	private String completeHalfLine(String line){
		while (line.length()<30){
			line += " ";
		}
		return line;
	}

	private String completeLine(String line){
		while (line.length()<59){
			line += " ";
		}
		return line + "*\n";
	}
}