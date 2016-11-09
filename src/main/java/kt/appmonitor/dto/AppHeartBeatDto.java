package kt.appmonitor.dto;

import org.joda.time.DateTime;


public class AppHeartBeatDto {
	
	private String appName;
	private DateTime timestamp;

	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
}
