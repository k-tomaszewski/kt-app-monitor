package kt.appmonitor.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


@Entity
@Table(name = "app_metrics")
public class AppMetrics {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "APP_ALIVE_ID", nullable = false, updatable = false)
	private AppAliveEntry appAliveEntry;
	
	@Column(name = "CONTENT", nullable = false, updatable = false)
	@NotNull
	private String content;
	
	@Column(name = "DATETIME", nullable = false, updatable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@NotNull
	private DateTime dataTime;

	
	public AppMetrics(AppAliveEntry appAliveEntry, String content, DateTime dataTime) {
		this.appAliveEntry = appAliveEntry;
		this.content = content;
		this.dataTime = dataTime;
	}
	
	// for Hibernate
	public AppMetrics() {
	}

	public String getContent() {
		return content;
	}

	public DateTime getDataTime() {
		return dataTime;
	}
}
