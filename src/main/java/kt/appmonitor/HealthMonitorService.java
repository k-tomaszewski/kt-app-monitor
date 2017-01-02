package kt.appmonitor;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AppHeartBeatDto;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class HealthMonitorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthMonitorService.class);
	
	private static final RowMapper<AppAliveEntry> appAliveEntryRowMapper = (ResultSet rs, int i) -> {
		DateTime fromTime = new DateTime(rs.getDate("START_DATETIME"));
		DateTime endTime = new DateTime(rs.getDate("END_DATETIME"));
		DateTime lastUpdateTime = new DateTime(rs.getDate("LAST_MOD_DATETIME"));
		return new AppAliveEntry(rs.getInt("ID"), fromTime, endTime, lastUpdateTime);
	};
	
	private DateTime startTime;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@PostConstruct
	public void postConstruct() {
		startTime = DateTime.now();
		System.out.println("HealthMonitorService created at " + startTime);
	}
	
	public void updateAppAliveEntry(String appName, AppHeartBeatDto heartBeatDto) {
		LOG.info("updateAppAliveEntry => appName: {}, heartBeatDto: {}", appName, heartBeatDto);
		

	}
	
	public List<AppAliveEntry> getAppAliveEntries(String appName) {
		return jdbcTemplate.query("select ID, START_DATETIME, END_DATETIME, LAST_MOD_DATETIME from app_alive where APP_NAME = ?",
				new Object[]{appName}, appAliveEntryRowMapper);
	}
	
	public Map<String, Object> getStatusVariables() {
		Map<String, Object> statusVariables = new TreeMap<>();
		
		System.getProperties().entrySet().forEach((entry) -> {
			String key = entry.getKey().toString();
			if (key.startsWith("java.runtime.")) {
				statusVariables.put(entry.getKey().toString(), entry.getValue());
			}
		});
		statusVariables.put("DYNO", System.getenv("DYNO"));
		statusVariables.put("app start timestamp", (startTime != null) ? startTime.toString() : null);
		statusVariables.put("runtime CPU count", Runtime.getRuntime().availableProcessors());
		statusVariables.put("runtime free memory", Runtime.getRuntime().freeMemory());
		statusVariables.put("runtime total memory", Runtime.getRuntime().totalMemory());
		
		return statusVariables;
	}
}
