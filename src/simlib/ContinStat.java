package simlib;

import java.io.FileWriter;
import java.io.IOException;

public class ContinStat {
	private float start;
	private float area;
	private float prevValue;
	private float prevTime;
	private float max;
	private float min;
	private String name;

	public ContinStat(float value, float present, String name) {
		start = present;
		area = 0;
		prevValue = value;
		prevTime = present;
		max = (float)-3.4E+38;
		min = (float)3.4E+38;
		this.name = name.toUpperCase();
	}

	public float getContinAve(float present) {
		area += (present - prevTime)*prevValue;
		prevTime = present;
		return area/(present - start);
	}

	public float getContinMax() {
		return max;
	}

	public float getContinMin() {
		return min;
	}

	public void recordContin(float value, float present) {
		area += (present - prevTime)*prevValue;
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

    public void report(FileWriter out, float present) throws IOException {
		out.write("REPORT DE "+name+" :\n");
		out.write("Promedio:\t"+getContinAve( present )+"\n");
		out.write("Valor mínimo: "+this.getContinMax()+"\n");
		out.write("Valor máximo: "+this.getContinMin()+"\n");
	}
}