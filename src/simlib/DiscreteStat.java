package simlib;

public class DiscreteStat {
	private float sum;
	private int numObs;
	private float max;
	private float min;

	public DiscreteStat() {
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
}