package kt.appmonitor.data;

import org.joda.time.DateTime;


public class AppAliveEntry {
	
	private Integer id;
	private String appName;
	private DateTime aliveFromTime;
	private DateTime aliveToTime;
	private DateTime lastModifiedTime;

	
	public AppAliveEntry(Integer id, DateTime aliveFromTime, DateTime aliveToTime, DateTime lastModifiedTime) {
		this.id = id;
		this.aliveFromTime = aliveFromTime;
		this.aliveToTime = aliveToTime;
		this.lastModifiedTime = lastModifiedTime;
	}
	
	public AppAliveEntry(String appName, DateTime aliveFromTime, DateTime aliveToTime, DateTime lastModifiedTime) {
		this.appName = appName;
		this.aliveFromTime = aliveFromTime;
		this.aliveToTime = aliveToTime;
		this.lastModifiedTime = lastModifiedTime;
	}	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public DateTime getAliveFromTime() {
		return aliveFromTime;
	}

	public DateTime getAliveToTime() {
		return aliveToTime;
	}

	public DateTime getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setAliveToTime(DateTime aliveToTime) {
		this.aliveToTime = aliveToTime;
	}

	public void setLastModifiedTime(DateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
}
