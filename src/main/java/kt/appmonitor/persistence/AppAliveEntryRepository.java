package kt.appmonitor.persistence;

import kt.appmonitor.data.AppAliveEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class AppAliveEntryRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	public AppAliveEntry findLastAppAliveEntry(String appName) {
		
	}
	
	public void create(AppAliveEntry appAliveEntry) {
		
	}
	
	public void update(AppAliveEntry appAliveEntry) {
		
	}
}
