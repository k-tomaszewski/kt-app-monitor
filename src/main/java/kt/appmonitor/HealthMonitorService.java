package kt.appmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.data.AppMetrics;
import kt.appmonitor.dto.AppHeartBeatDto;
import kt.appmonitor.persistence.AppAliveEntryRepository;
import kt.appmonitor.persistence.AppMetricsRepository;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HealthMonitorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthMonitorService.class);
	
	private DateTime startTime;
	private ReadableDuration betweenHeartBeatsMaxDuration;
	
	@Autowired
	private AppAliveEntryRepository appAliveEntryRepo;
	
	@Autowired
	private AppMetricsRepository appMetricsRepo;
	
	@Value("${max-minutes-beetween-heartbeats}")
	private int betweenHeartBeatsMaxMinutes;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private SignatureVerifier signatureVerifier;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	
	@PostConstruct
	public void postConstruct() {
		startTime = DateTime.now();
		betweenHeartBeatsMaxDuration = Duration.standardMinutes(betweenHeartBeatsMaxMinutes);
		LOG.info("HealthMonitorService created at {}. betweenHeartBeatsMaxDuration: {}", startTime, betweenHeartBeatsMaxDuration);
	}
	
	@Transactional
	public void updateAppAliveEntry(String appName, AppHeartBeatDto heartBeatDto) {
		LOG.info("updateAppAliveEntry => appName: {}, heartBeatDto: {}", appName, heartBeatDto);
		
		final AppAliveEntry lastEntry = appAliveEntryRepo.findLastAppAliveEntry(appName);
		if (lastEntry != null) {
			Validate.isTrue(heartBeatDto.getTimestamp().isAfter(lastEntry.getLastHeartBeatTime()),
					"Input data timestamp (%s) not after last entry timestamp (%s)",
					heartBeatDto.getTimestamp(), lastEntry.getAliveToTime());
		}
		
		// check input data signature
		Validate.isTrue(signatureVerifier.hasValidSignature(heartBeatDto),
				"Input data has invalid signature");
		
		final DateTime now = DateTime.now();
		AppAliveEntry currentEntry;
		
		if (lastEntry == null || isLastAppAliveEntryTooOld(lastEntry, heartBeatDto.getTimestamp())) {
			currentEntry = appAliveEntryRepo.create(new AppAliveEntry(appName, now, now, heartBeatDto.getTimestamp()));
		} else {
			lastEntry.setAliveToTime(now);
			lastEntry.setLastHeartBeatTime(heartBeatDto.getTimestamp());
			lastEntry.incrementHeartBeatCount();
			appAliveEntryRepo.update(lastEntry);
			currentEntry = lastEntry;
		}
		
		// record additional data
		String metricsJson = generateJson(heartBeatDto.getMetrics());
		if (metricsJson != null) {
			appMetricsRepo.create(new AppMetrics(currentEntry, metricsJson, now));
		}
	}
	
	String generateJson(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException ex) {
			LOG.error("Cannot generate JSON representation of heartbeat metrics data.", ex);
			return null;
		}
	}
	
	boolean isLastAppAliveEntryTooOld(AppAliveEntry entry, DateTime newHeartBeatTime) {
		return (new Duration(entry.getAliveToTime(), newHeartBeatTime)).isLongerThan(betweenHeartBeatsMaxDuration);
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
		statusVariables.put("app start timestamp", startTime);
		statusVariables.put("runtime CPU count", Runtime.getRuntime().availableProcessors());
		statusVariables.put("runtime free memory", Runtime.getRuntime().freeMemory());
		statusVariables.put("runtime total memory", Runtime.getRuntime().totalMemory());
		statusVariables.put("active profiles", env.getActiveProfiles());
		
		return statusVariables;
	}
}
