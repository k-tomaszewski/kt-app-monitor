package kt.appmonitor.dto;

import org.joda.time.DateTime;


public class AlertDto {
	
	private String appName;
	private DateTime eventTime;
	private String title;
	private Object details;

	
	public AlertDto(String appName, DateTime eventTime, String title, Object details) {
		this.appName = appName;
		this.eventTime = eventTime;
		this.title = title;
		this.details = details;
	}

	public String getAppName() {
		return appName;
	}

	public DateTime getEventTime() {
		return eventTime;
	}

	public String getTitle() {
		return title;
	}

	public Object getDetails() {
		return details;
	}
}
