package kt.appmonitor.dto;

import org.joda.time.DateTime;


public class AlertDto {
	
	private String appName;
	private DateTime eventTime;
	private AlertType type;
	private Object details;
	private String id;

	
	public AlertDto(String appName, DateTime eventTime, AlertType type, Object details, String id) {
		this.appName = appName;
		this.eventTime = eventTime;
		this.type = type;
		this.details = details;
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public DateTime getEventTime() {
		return eventTime;
	}

	public AlertType getType() {
		return type;
	}

	public Object getDetails() {
		return details;
	}

	public String getId() {
		return id;
	}
}
