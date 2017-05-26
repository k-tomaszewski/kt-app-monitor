package kt.appmonitor.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	
	private static final ResultSetExtractor<AppAliveEntry> appAliveExtractor =
			(ResultSet rs) -> (rs.next() ? mapCurrentToAppAliveEntry(rs) : null);
	private static final RowMapper<AppAliveEntry> appAliveEntryRowMapper = (ResultSet rs, int i) -> mapCurrentToAppAliveEntry(rs);	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	public AppAliveEntry findLastAppAliveEntry(String appName) {
		return jdbcTemplate.query(
			"select * from app_alive where APP_NAME = ? order by END_DATETIME desc limit 1",
			new Object[]{appName},
			appAliveExtractor);
	}
	
	public List<AppAliveEntry> findAppAliveEntries(String appName) {
		return jdbcTemplate.query(
			"select ID, START_DATETIME, END_DATETIME, LAST_HEARTBEAT_DATETIME, HEARTBEAT_COUNT from app_alive where APP_NAME = ?",
			new Object[]{appName},
			appAliveEntryRowMapper);	
	}
	
	public void create(AppAliveEntry appAlive) {
		jdbcTemplate.update(
			"insert into app_alive (APP_NAME, START_DATETIME, END_DATETIME, LAST_HEARTBEAT_DATETIME, HEARTBEAT_COUNT) values (?,?,?,?,?)",
			appAlive.getAppName(),
			asTimestamp(appAlive.getAliveFromTime()),
			asTimestamp(appAlive.getAliveToTime()),
			asTimestamp(appAlive.getLastHeartBeatTime()),
			appAlive.getHeartBeatCount());
	}
	
	public void update(AppAliveEntry appAlive) {
		jdbcTemplate.update(
			"update app_alive set END_DATETIME = ?, LAST_HEARTBEAT_DATETIME = ?, HEARTBEAT_COUNT = ? where id = ?",
			asTimestamp(appAlive.getAliveToTime()),
			asTimestamp(appAlive.getLastHeartBeatTime()),
			appAlive.getHeartBeatCount(),
			appAlive.getId());
	}
	
	private static AppAliveEntry mapCurrentToAppAliveEntry(ResultSet rs) throws SQLException {
		return new AppAliveEntry(
			rs.getInt("ID"),
			new DateTime(rs.getTimestamp("START_DATETIME")),
			new DateTime(rs.getTimestamp("END_DATETIME")),
			new DateTime(rs.getTimestamp("LAST_HEARTBEAT_DATETIME")),
			rs.getInt("HEARTBEAT_COUNT"));		
	}
	
	private static Timestamp asTimestamp(DateTime dt) {
		return new Timestamp(dt.getMillis());
	}	
}
