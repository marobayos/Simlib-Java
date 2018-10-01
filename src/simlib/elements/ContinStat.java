package simlib.elements;

import simlib.io.SimWriter;

import java.io.IOException;
import static simlib.SimLib.*;

public class ContinStat extends Element{
	private Float prevValue;
	private Float max;
	private Float min;

	public ContinStat( String name, Float value) {
		super( name );
		prevValue = max = min = value;
		this.name = name;
	}

	public ContinStat( String name, int value){
		this(name, (float)value);
	}

	public ContinStat( String name, double value){
		super( name );
		prevValue = max = min = (float)value;
	}

	public ContinStat( String name) {
		this(name, null);
	}

	public double getAverage() {
		area += (simTime - lastUpdate)*(double)prevValue;
		lastUpdate = simTime;
		return area/(simTime - start);
	}

	public Float getMax() {
		return max;
	}

	public Float getMin() {
		return min;
	}

	public void record(float value) {
		update();
		if (this.prevValue == null){
			max = min = value;
		}
		if (value > max)
			max = value;
		else if (value < min)
			min = value;
		prevValue = value;
	}


	public float getValue() {
	    return prevValue;
    }

    public void report(SimWriter out) throws IOException {
		this.update();
		out.write("************************************************************\n");
		out.write(this.completeLine("*  CONTINUES STATISTIC "+name));
		out.write("************************************************************\n");
		out.write(completeLine(this.completeHalfLine("*  Min = "+min)+"  Max = "+max));
		out.write(this.completeLine("*  Time interval = "+start+" - "+simTime));
		out.write(this.completeLine("*  Average = "+this.getAverage()));
		out.write("************************************************************\n\n");
	}

	@Override
	void update() {
		if( prevValue != null )
		area += (simTime - lastUpdate)*(double)prevValue;
		lastUpdate = simTime;
	}
}