package kt.appmonitor.persistence;

import kt.appmonitor.data.AppMetrics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class AppMetricsRepository {
	
	private static final Class<AppMetrics> ENTITY_CLASS = AppMetrics.class;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	
	public void create(AppMetrics appMetrics) {
		getSession().save(appMetrics);
	}
	
	public long count() {
		return getSession()
			.createQuery("select count(x) from " + ENTITY_CLASS.getName() + " x", Long.class)
			.getSingleResult();
	}
	
	public int deleteOlderThan(DateTime thresholdTime) {
		return getSession()
			.createQuery("delete from " + ENTITY_CLASS.getName() + " x where x.dataTime < :thresholdTime")
			.setParameter("thresholdTime", thresholdTime)
			.executeUpdate();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}	
}
