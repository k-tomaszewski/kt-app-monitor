package kt.appmonitor.persistence;

import kt.appmonitor.data.AppMetrics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}	
}
