package kt.appmonitor.persistence;

import java.util.List;
import kt.appmonitor.data.AppAliveEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class AppAliveEntryRepository {
	
	private static final Class<AppAliveEntry> ENTITY_CLASS = AppAliveEntry.class;

	@Autowired
	private SessionFactory sessionFactory;
	
	
	@Transactional(readOnly = true)
	public AppAliveEntry findLastAppAliveEntry(String appName) {
		return getSession()
			.createQuery("select x from " + ENTITY_CLASS.getName() + " x where x.appName = :appName"
				+ " order by x.aliveToTime DESC", ENTITY_CLASS)
			.setMaxResults(1)
			.setParameter("appName", appName)
			.getResultList()
			.stream().findAny().orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<AppAliveEntry> findAppAliveEntries(String appName) {
		return getSession()
			.createQuery("select distinct x from " + ENTITY_CLASS.getName()
				+ " x left outer join fetch x.metricsEntries where x.appName = :appName"
				+ " order by x.aliveToTime DESC",
				ENTITY_CLASS)
			.setParameter("appName", appName)
			.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
			.getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<AppAliveEntry> findAllAppAliveEntries() {
		return getSession()
			.createQuery("select x from " + ENTITY_CLASS.getName() + " x order by x.aliveToTime DESC",
				ENTITY_CLASS)
			.getResultList();
	}
	
	public AppAliveEntry create(AppAliveEntry appAlive) {
		getSession().save(appAlive);
		return appAlive;
	}
	
	public void update(AppAliveEntry appAlive) {
		getSession().update(appAlive);
	}
	
	public long count() {
		return getSession()
			.createQuery("select count(x) from " + ENTITY_CLASS.getName() + " x", Long.class)
			.getSingleResult();
	}
	
	public int deleteOlderThan(DateTime thresholdTime) {
		return getSession()
			.createQuery("delete from " + ENTITY_CLASS.getName() + " x where x.aliveToTime < :thresholdTime")
			.setParameter("thresholdTime", thresholdTime)
			.executeUpdate();
	}	
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}	
}
