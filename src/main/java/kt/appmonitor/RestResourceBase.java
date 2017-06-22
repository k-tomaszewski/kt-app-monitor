package kt.appmonitor;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import org.springframework.web.context.support.WebApplicationContextUtils;


public abstract class RestResourceBase {
	
	@Context
	private ServletContext servletContext;
	
	protected HealthMonitorService getHealthMonitorService() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
				.getBean(HealthMonitorService.class);
	}	
}
