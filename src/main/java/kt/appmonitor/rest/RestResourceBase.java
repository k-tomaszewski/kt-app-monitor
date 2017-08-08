package kt.appmonitor.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import org.springframework.web.context.support.WebApplicationContextUtils;


public abstract class RestResourceBase {
	
	@Context
	private ServletContext servletContext;
	
	protected <T> T getBean(Class<T> type) {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getBean(type);
	}
}
