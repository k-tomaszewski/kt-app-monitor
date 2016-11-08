package kt.appmonitor;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class HealthMonitorService {
	
	private DateTime startTime;
	/*
	@Autowired
	private DataSource dataSource;
	*/
	
	@Autowired
	@Qualifier("dbUrl")
	private URI dbUri;
	
	@PostConstruct
	public void postConstruct() {
		startTime = DateTime.now();
		System.out.println("HealthMonitorService created at " + startTime);
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
		
		statusVariables.put("dbURI", dbUri.toString());
		//statusVariables.put("data-source-class", dataSource.getClass().getName());
		statusVariables.put("test-db-url", "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath());
		
		return statusVariables;
	}
}
