package kt.appmonitor;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import kt.appmonitor.dto.AppHeartBeatDto;
import org.springframework.web.context.support.WebApplicationContextUtils;


@Path("v1")
@Produces(MediaType.APPLICATION_JSON)
public class MainResource {
	
	@Context
	private ServletContext servletContext;

	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAppAlive(AppHeartBeatDto appHeartBeat) {
		getHealthMonitorService().updateAppAliveEntry(appHeartBeat.getAppName(), appHeartBeat.getTimestamp());
	}
	
    @GET
    public Map<String, Object> getStatus() {
		HealthMonitorService service = getHealthMonitorService();
		return service.getStatusVariables();
    }
	
	private HealthMonitorService getHealthMonitorService() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
				.getBean(HealthMonitorService.class);
	}
}
