package simlib.elements;

import simlib.io.SimWriter;

import java.io.IOException;

public class ContinStat{
	private Float start;
	private float area;
	private Float prevValue;
	private float prevTime;
	private Float max;
	private Float min;
	private String name;
	private Timer timer;

	public ContinStat( String name, Float value, Timer timer) {
		start = timer.getTime();
		area = 0;
		prevValue = max = min = value;
		prevTime = start;
		this.name = name;
		this.timer = timer;
	}

	public ContinStat( String name, int value, Timer timer){
		this(name, (float)value, timer);
	}

	public ContinStat( String name, double value, Timer timer){
		start = timer.getTime();
		area = 0;
		prevValue = max = min = (float)value;
		prevTime = start;
		this.name = name.toUpperCase();
		this.timer = timer;
	}

	public ContinStat( String name, Timer timer) {
		this(name, null, timer);
	}

	public double getContinAve() {
		area += (timer.getTime() - prevTime)*(double)prevValue;
		prevTime = timer.getTime();
		return area/(timer.getTime() - start);
	}

	public Float getContinMax() {
		return max;
	}

	public Float getContinMin() {
		return min;
	}

	public void recordContin(float value) {
		area += (this.timer.getTime() - prevTime)*(double)prevValue;
		if (this.prevValue == null){
			min = value;
		}
		if (value > max)
			max = value;
		if (value < min)
			min = value;
		prevValue = value;
		prevTime = timer.getTime();
	}

	public float getValue() {
	    return prevValue;
    }

    public void report(SimWriter out) throws IOException {
		out.write("************************************************************\n");
		out.write(this.completeLine("*  STATISTIC "+name));
		out.write("************************************************************\n");
		out.write(completeLine(this.completeHalfLine("*  Min = "+min)+"  Max = "+max));
		out.write(this.completeLine("*  Time interval = "+start+" - "+timer.getTime()));
		out.write(this.completeLine("*  Average = "+this.getContinAve()));
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