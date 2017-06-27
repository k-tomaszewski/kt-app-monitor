package kt.appmonitor;

import kt.appmonitor.persistence.AppAliveEntryRepository;
import kt.appmonitor.persistence.AppMetricsRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class DbPrunningStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbPrunningStrategy.class);
	
	private DateTime lastPrunningTime;
	
	@Autowired
	private AppMetricsRepository appMetricsRepo;
	
	@Autowired
	private AppAliveEntryRepository appAliveEntryRepo;
	
	
	@Async
	public void run() {
		final DateTime now = DateTime.now();
		if (lastPrunningTime != null && lastPrunningTime.plusDays(1).isAfter(now)) {
			LOG.info("DB prunning was executed at {} so it's not needed now.", lastPrunningTime);
			return;
		}
		LOG.info("Starting DB prunning...");
		
		final DateTime thresholdTime = now.minusMonths(2);
		
		final int removedAppMetricsRecords = appMetricsRepo.deleteOlderThan(thresholdTime);
		LOG.info("Removed records with app metrics: {}", removedAppMetricsRecords);
		
		final int removedAppAliveRecords = appAliveEntryRepo.deleteOlderThan(thresholdTime);
		LOG.info("Removed app-alive records: {}", removedAppAliveRecords);

		lastPrunningTime = now;
		LOG.info("All records: {}", appAliveEntryRepo.count() + appMetricsRepo.count());
	}
}
