package Coconut;

public class Timer {
	private long start = -1;
	private long duration = 0;
	private boolean loop;

	public Timer() {}

	public Timer(long duration, boolean loop) {
		this.duration = duration;
		this.loop = loop;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public boolean isExpired() {
		if(start != -1) {
			boolean expired = System.currentTimeMillis() - start > duration;
			if(loop && expired)
				start += duration;
			return expired;
		}
		else
			return true;
	}
}
