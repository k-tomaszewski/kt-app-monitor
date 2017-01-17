package kt.appmonitor.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import kt.appmonitor.data.AppAliveEntry;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class AppAliveEntryRepository {
	
	private static final ResultSetExtractor<AppAliveEntry> appAliveExtractor = (ResultSet rs) -> mapCurrentToAppAliveEntry(rs);
	private static final RowMapper<AppAliveEntry> appAliveEntryRowMapper = (ResultSet rs, int i) -> mapCurrentToAppAliveEntry(rs);	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	public AppAliveEntry findLastAppAliveEntry(String appName) {
		return jdbcTemplate.query("select * from app_alive where APP_NAME = ? order by END_DATETIME desc limit 1",
				new Object[]{appName}, appAliveExtractor);
	}
	
	public List<AppAliveEntry> findAppAliveEntries(String appName) {
		return jdbcTemplate.query("select ID, START_DATETIME, END_DATETIME, LAST_MOD_DATETIME from app_alive where APP_NAME = ?",
				new Object[]{appName}, appAliveEntryRowMapper);		
	}
	
	public AppAliveEntry create(AppAliveEntry appAlive) {
		Integer id = jdbcTemplate.queryForObject(
				"insert into app_alive (APP_NAME, START_DATETIME, END_DATETIME, LAST_MOD_DATETIME) values (?,?,?,?) RETURNING id",
				Integer.class,
				appAlive.getAppName(), appAlive.getAliveFromTime(), appAlive.getAliveToTime(), appAlive.getLastModifiedTime());
		appAlive.setId(id);
		return appAlive;
	}
	
	public void update(AppAliveEntry appAlive) {
		jdbcTemplate.update("update app_alive set END_DATETIME = ?, LAST_MOD_DATETIME = ? where id = ?",
				appAlive.getAliveToTime(), appAlive.getLastModifiedTime(), appAlive.getId());
	}
	
	private static AppAliveEntry mapCurrentToAppAliveEntry(ResultSet rs) throws SQLException {
		DateTime fromTime = new DateTime(rs.getDate("START_DATETIME"));
		DateTime endTime = new DateTime(rs.getDate("END_DATETIME"));
		DateTime lastUpdateTime = new DateTime(rs.getDate("LAST_MOD_DATETIME"));
		return new AppAliveEntry(rs.getInt("ID"), fromTime, endTime, lastUpdateTime);		
	}
}
