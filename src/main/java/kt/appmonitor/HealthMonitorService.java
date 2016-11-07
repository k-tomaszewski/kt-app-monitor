package kt.appmonitor;

import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;


@Service
public class HealthMonitorService {
	
	@PostConstruct
	public void postConstruct() {
		System.out.println("HealthMonitorService bean created - " + new Date());
	}
	
}
