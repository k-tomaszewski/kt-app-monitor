package kt.common.logback;


public class LogMessage {
	
	final String text;
	final long time;

	public LogMessage(String text, long time) {
		this.text = text;
		this.time = time;
	}

	public String getText() {
		return text;
	}

	public long getTime() {
		return time;
	}
}
