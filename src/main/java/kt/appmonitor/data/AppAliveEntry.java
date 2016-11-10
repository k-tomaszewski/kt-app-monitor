package kt.appmonitor.data;

import org.joda.time.DateTime;


public class AppAliveEntry {
	
	private Integer id;
	private DateTime aliveFromTime;
	private DateTime aliveToTime;
	private DateTime lastModifiedTime;

	
	public AppAliveEntry(Integer id, DateTime aliveFromTime, DateTime aliveToTime, DateTime lastModifiedTime) {
		this.id = id;
		this.aliveFromTime = aliveFromTime;
		this.aliveToTime = aliveToTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	public Integer getId() {
		return id;
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
}
