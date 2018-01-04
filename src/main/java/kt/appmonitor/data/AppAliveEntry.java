package kt.appmonitor.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;


@Entity
@Table(name = "app_alive")
@JsonInclude(Include.NON_NULL)
public class AppAliveEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "APP_NAME", nullable = false, updatable = false)
	private String appName;
	
	/**
	 * Server timestamp of first report accounted for this AppAliveEntry
	 */
	@Column(name = "START_DATETIME", nullable = false, updatable = false)
	@NotNull
	private DateTime aliveFromTime;
	
	/**
	 * Server timestamp of last report account for this AppAliveEntry
	 */
	@Column(name = "END_DATETIME", nullable = false)
	@NotNull
	private DateTime aliveToTime;
	
	/**
	 * Timestamp from last heart beat accounted for this AppAliveEntry
	 */
	@Column(name = "LAST_HEARTBEAT_DATETIME", nullable = false)
	private DateTime lastHeartBeatTime;
	
	@Column(name = "HEARTBEAT_COUNT", nullable = false)
	private int heartBeatCount;
	
	@OneToMany(mappedBy = "appAliveEntry", cascade = CascadeType.ALL, orphanRemoval = true)	// lazy
	@OrderBy("dataTime ASC")
    private List<AppMetrics> metricsEntries = new ArrayList<>();

	
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
	
	// for Hibernate
	public AppAliveEntry() {
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

	public List<AppMetrics> getMetricsEntries() {
		return metricsEntries;
	}
}
