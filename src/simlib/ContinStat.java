package simlib;

public class ContinStat {
	private float start;
	private float area;
	private float prevValue;
	private float prevTime;
	private float max;
	private float min;

	public ContinStat(float value, float present) {
		start = present;
		area = 0;
		prevValue = value;
		prevTime = present;
		max = (float)-3.4E+38;
		min = (float)3.4E+38;
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
}