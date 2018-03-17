package simlib;

import java.io.BufferedWriter;
import java.io.IOException;

public class ContinStat{
	private Float start;
	private float area;
	private Float prevValue;
	private float prevTime;
	private Float max;
	private Float min;
	private String name;

	public ContinStat(Float value, float present, String name) {
		start = present;
		area = 0;
		prevValue = max = min = value;
		prevTime = present;
		this.name = name.toUpperCase();
	}

	public ContinStat(float present, String name) {
		this(null, present, name);
	}

	public double getContinAve(float present) {
		area += (present - prevTime)*(double)prevValue;
		prevTime = present;
		return area/(present - start);
	}

	public Float getContinMax() {
		return max;
	}

	public Float getContinMin() {
		return min;
	}

	public void recordContin(float value, float present) {
		area += (present - prevTime)*(double)prevValue;
		if (this.prevValue == null){
			min = value;
		}
		if (value > max)
			max = value;
		if (value < min)
			min = value;
		prevValue = value;
		prevTime = present;
	}

	public float getValue() {
	    return prevValue;
    }

    public void report(BufferedWriter out, float present) throws IOException {
		out.write("REPORTE DEL "+name+":\n");
		out.write("\tPromedio:\t"+getContinAve( present )+"\n");
		out.write("\tValor mínimo: "+this.getContinMax()+"\n");
		out.write("\tValor máximo: "+this.getContinMin()+"\n\n");
	}
}