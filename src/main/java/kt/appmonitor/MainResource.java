package kt.appmonitor;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.springframework.web.context.support.WebApplicationContextUtils;


@Path("v1")
public class MainResource {
	
	@Context
	private ServletContext servletContext;

	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
		HealthMonitorService service = getHealthMonitorService();
        return "Hello, Heroku! [" + service + ']';
    }
	
	private HealthMonitorService getHealthMonitorService() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
				.getBean(HealthMonitorService.class);
	}
}
