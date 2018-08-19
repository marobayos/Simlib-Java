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
	private Timer timer;

	public ContinStat( String name, Float value, Timer timer) {
		start = timer.getTime();
		area = 0;
		prevValue = max = min = value;
		prevTime = start;
		this.name = name.toUpperCase();
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
		out.write("REPORTE DEL "+name+":\n");
		out.write("\tPromedio:\t"+getContinAve( )+"\n");
		out.write("\tValor mínimo: "+this.getContinMax()+"\n");
		out.write("\tValor máximo: "+this.getContinMin()+"\n\n");
	}
}