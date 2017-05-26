package kt.appmonitor.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;


@JsonInclude(Include.NON_NULL)
public class AppAliveEntry {
	
	private Integer id;
	private String appName;
	
	/**
	 * Server timestamp of first report accounted for this AppAliveEntry
	 */
	@NotNull
	private final DateTime aliveFromTime;
	
	/**
	 * Server timestamp of last report account for this AppAliveEntry
	 */
	@NotNull
	private DateTime aliveToTime;
	
	/**
	 * Timestamp from last heart beat accounted for this AppAliveEntry
	 */
	private DateTime lastHeartBeatTime;
	
	private int heartBeatCount;

	
	public AppAliveEntry(Integer id, DateTime aliveFromTime, DateTime aliveToTime, DateTime lastHeartBeatTime,
			int heartBeatCount) {
		this.id = id;
		this.aliveFromTime = aliveFromTime;
		this.aliveToTime = aliveToTime;
		this.lastHeartBeatTime = lastHeartBeatTime;
		this.heartBeatCount = heartBeatCount;
	}
	
	public AppAliveEntry(String appName, DateTime aliveFromTime, DateTime aliveToTime, DateTime lastHeartBeatTime) {
		this.appName = appName;
		this.aliveFromTime = aliveFromTime;
		this.aliveToTime = aliveToTime;
		this.lastHeartBeatTime = lastHeartBeatTime;
		heartBeatCount = 1;
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

	public DateTime getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public void setAliveToTime(DateTime aliveToTime) {
		this.aliveToTime = aliveToTime;
	}

	public void setLastHeartBeatTime(DateTime lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
	}

	public int getHeartBeatCount() {
		return heartBeatCount;
	}
	
	public void incrementHeartBeatCount() {
		++heartBeatCount;
	}
}
