package kt.appmonitor.persistence;

import java.util.List;
import kt.appmonitor.data.AppAliveEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class AppAliveEntryRepository {
	
	private static final Class<AppAliveEntry> ENTITY_CLASS = AppAliveEntry.class;

	@Autowired
	private SessionFactory sessionFactory;
	
	
	public AppAliveEntry findLastAppAliveEntry(String appName) {
		return getSession()
			.createQuery("select x from " + ENTITY_CLASS.getName() + " x where x.appName = :appName "
				+ "order by x.aliveToTime DESC", ENTITY_CLASS)
			.setMaxResults(1)
			.setParameter("appName", appName)
			.getResultList()
			.stream().findAny().orElse(null);
	}
	
	public List<AppAliveEntry> findAppAliveEntries(String appName) {
		return getSession()
			.createQuery("select distinct x from " + ENTITY_CLASS.getName() + " x left outer join fetch x.metricsEntries where x.appName = :appName",
				ENTITY_CLASS)
			.setParameter("appName", appName)
			.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
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
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}	
}
