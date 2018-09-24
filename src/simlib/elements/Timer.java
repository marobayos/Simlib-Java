package simlib.elements;

public class Timer {
	private float present;
	private static Timer timer = new Timer();

	private Timer() {
		present = 0;
	}

	public static Timer getTimer(){
		if( timer == null){
			//timer = new Timer2();
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