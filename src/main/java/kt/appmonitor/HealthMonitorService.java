package kt.appmonitor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AppHeartBeatDto;
import kt.appmonitor.persistence.AppAliveEntryRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HealthMonitorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthMonitorService.class);
	
	private DateTime startTime;
	
	@Autowired
	private AppAliveEntryRepository appAliveEntryRepo;
	
	
	@PostConstruct
	public void postConstruct() {
		startTime = DateTime.now();
		System.out.println("HealthMonitorService created at " + startTime);
	}
	
	@Transactional
	public void updateAppAliveEntry(String appName, AppHeartBeatDto heartBeatDto) {
		LOG.info("updateAppAliveEntry => appName: {}, heartBeatDto: {}", appName, heartBeatDto);
		
		AppAliveEntry existingEntry = appAliveEntryRepo.findLastAppAliveEntry(appName);
		if (existingEntry == null) {	// OR it is too old
			appAliveEntryRepo.create(new AppAliveEntry(appName, heartBeatDto.getTimestamp(), heartBeatDto.getTimestamp(), DateTime.now()));
		} else {
			existingEntry.setAliveToTime(heartBeatDto.getTimestamp());
			existingEntry.setLastModifiedTime(DateTime.now());
			appAliveEntryRepo.update(existingEntry);
		}
	}
	
	@Transactional(readOnly = true)
	public List<AppAliveEntry> getAppAliveEntries(String appName) {
		return appAliveEntryRepo.findAppAliveEntries(appName);
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
