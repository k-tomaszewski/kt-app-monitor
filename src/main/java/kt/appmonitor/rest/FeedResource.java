package kt.appmonitor.rest;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import kt.appmonitor.AlertingService;


@Path("atom")
@Produces(MediaType.APPLICATION_ATOM_XML)
public class FeedResource extends RestResourceBase {
	
	@GET
	@Path("{appName}")
	public Response getAlertsFor(@PathParam("appName") String appName) {
		StreamingOutput streamingOutput = (OutputStream output) -> {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
			
			SyndFeed feed = getFeedFor(appName);
			try {
				(new SyndFeedOutput()).output(feed, writer);
			} catch (Exception ex) {
				throw new RuntimeException("Cannot generate Atom feed output", ex);
			}
			
			// important!
			writer.flush();
		};
		return Response.ok(streamingOutput).build();
	}
	
	protected SyndFeed getFeedFor(String appName) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");
		feed.setEncoding("UTF-8");
		feed.setTitle(String.format("Monitoring of '%s' application", appName));
		feed.setDescription(String.format("Status notifications from '%s' application by kt-app-monitor.", appName));
		feed.setLink("https://github.com/k-tomaszewski/kt-app-monitor");

		// entries
		feed.setEntries(getAlertingService().getAlertsFor(appName).stream().map(alert -> {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(alert.getTitle());
			entry.setAuthor(appName);
			entry.setPublishedDate(alert.getEventTime().toDate());
			if (alert.getDetails() != null) {
				SyndContent description = new SyndContentImpl();
				description.setType("text/plain");
				description.setValue(alert.getDetails().toString());
				entry.setDescription(description);
			}
			return entry;
		}).collect(Collectors.toList()));

		return feed;
	}
	
	private AlertingService getAlertingService() {
		return getBean(AlertingService.class);
	}
}
