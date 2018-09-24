package simlib.elements;

public class Timer {
	private float present;
	private static final Timer timer;

	public Timer() {
		present = 0;
	}
	
	public static Timer getTimer(){
		if( timer == null){
			timer = new Timer();
		}
		return timer;
	}

	public void setTime(float time) {
		present = time;
	}

	public float getTime() {
		return present;
	}
}
