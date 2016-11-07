package kt.appmonitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;


@Service
public class HealthMonitorService {
	
	private DateTime startTime;
	
	
	@PostConstruct
	public void postConstruct() {
		startTime = DateTime.now();
		System.out.println("HealthMonitorService created at " + startTime);
	}
	
	public Map<String, Object> getStatusVariables() {
		Map<String, Object> statusVariables = new HashMap<>();
		
		System.getProperties().entrySet().forEach((entry) -> {
			statusVariables.put("PROP " + entry.getKey().toString(), entry.getValue());
		});
		
		System.getenv().forEach((key, value) -> {
			statusVariables.put("ENV " + key, value);
		});
		
		statusVariables.put("start timestamp", startTime);
		statusVariables.put("runtime CPU count", Runtime.getRuntime().availableProcessors());
		statusVariables.put("runtime free memory", Runtime.getRuntime().freeMemory());
		statusVariables.put("runtime total memory", Runtime.getRuntime().totalMemory());
		
		return statusVariables;
	}
}
