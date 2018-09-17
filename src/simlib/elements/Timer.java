package simlib.elements;

public class Timer {
	private float present;

	public Timer() {
		present = 0;
	}

	public void setTime(float time) {
		present = time;
	}

	public float getTime() {
		return present;
	}
}