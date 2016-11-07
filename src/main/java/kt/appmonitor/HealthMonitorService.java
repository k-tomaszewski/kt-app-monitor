package kt.appmonitor;

import java.util.Map;
import java.util.TreeMap;
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
		Map<String, Object> statusVariables = new TreeMap<>();
		
		System.getProperties().entrySet().forEach((entry) -> {
			String key = entry.getKey().toString();
			if (key.startsWith("java.runtime.") || key.startsWith("java.vm.") || key.startsWith("os.")) {
				statusVariables.put("PROP " + entry.getKey().toString(), entry.getValue());
			}
		});
		
		System.getenv().forEach((key, value) -> {
			if (key.startsWith("DATABASE") || key.startsWith("DYNO")) {
				statusVariables.put("ENV " + key, value);
			}
		});
		
		statusVariables.put("app start timestamp", (startTime != null) ? startTime.toString() : null);
		statusVariables.put("runtime CPU count", Runtime.getRuntime().availableProcessors());
		statusVariables.put("runtime free memory", Runtime.getRuntime().freeMemory());
		statusVariables.put("runtime total memory", Runtime.getRuntime().totalMemory());
		
		return statusVariables;
	}
}
