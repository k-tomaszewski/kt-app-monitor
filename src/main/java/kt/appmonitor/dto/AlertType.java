package kt.appmonitor.dto;


public enum AlertType {
	HEARTBEAT_LOST("Heartbeat lost!"),
	HEARTBEAT_RECOVERED("Heartbeat recovered.");
	
	private final String title;

	private AlertType(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
