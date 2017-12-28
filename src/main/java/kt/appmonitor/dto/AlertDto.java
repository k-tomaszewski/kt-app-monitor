package kt.appmonitor.dto;

import org.joda.time.DateTime;


public class AlertDto {
	
	private final String appName;
	private final DateTime eventTime;
	private final DateTime updateTime;
	private final AlertType type;
	private final Object details;
	private final String id;

	
	public AlertDto(String appName, DateTime eventTime, DateTime updateTime, AlertType type, Object details, String id) {
		this.appName = appName;
		this.eventTime = eventTime;
		this.updateTime = updateTime;
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

	public DateTime getUpdateTime() {
		return updateTime;
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
