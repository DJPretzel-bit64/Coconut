package Coconut;

public class Timer {
	private long start = -1;
	private long duration = 0;

	public Timer() {}

	public Timer(long duration) {
		this.duration = duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public boolean isExpired() {
		if(start != -1)
			return System.currentTimeMillis() - start > duration;
		else
			return true;
	}
}
