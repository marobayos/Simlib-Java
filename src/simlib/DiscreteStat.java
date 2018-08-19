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

	public void report(SimWriter out) throws IOException {
		out.write("************************************************************\n");
		out.write(this.completeLine("*  STATISTIC "+name));
		out.write("************************************************************\n");
		out.write(completeLine(this.completeHalfLine("*  Min = "+min)+"  Max = "+max));
		out.write(this.completeLine("*  Records = "+numObs));
		out.write(this.completeLine("*  Average = "+this.getDiscreteAverage()));
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