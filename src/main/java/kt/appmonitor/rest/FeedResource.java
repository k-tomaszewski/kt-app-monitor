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
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import kt.appmonitor.AlertingService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("atom")
@Produces(MediaType.APPLICATION_ATOM_XML)
public class FeedResource extends RestResourceBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(FeedResource.class);

	@GET
	@Path("{appName}")
	public Response getAlertsFor(@PathParam("appName") String appName, @HeaderParam("Accept-Language") String acceptLangHeader) {
		StreamingOutput streamingOutput = (OutputStream output) -> {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

			SyndFeed feed = getFeedFor(appName, readLocaleFromAcceptLangHeader(acceptLangHeader));
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

	protected SyndFeed getFeedFor(String appName, Locale locale) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");
		feed.setEncoding("UTF-8");
		feed.setTitle(String.format("Monitoring of '%s' application", appName));
		feed.setDescription(String.format("Status notifications from '%s' application by kt-app-monitor.", appName));
		feed.setUri("kt-app-monitor.herokuapp.com/" + appName);

		// entries
		feed.setEntries(getAlertingService().getAlertsFor(appName, locale).stream().map(alert -> {
			SyndEntry entry = new SyndEntryImpl();
			entry.setUri(feed.getUri() + '/' + alert.getId());
			entry.setTitle(alert.getType().getTitle());
			entry.setPublishedDate(alert.getEventTime().toDate());
			if (alert.getDetails() != null) {
				
				SyndContent description = new SyndContentImpl();
				description.setType("text/plain");
				description.setValue(alert.getDetails().toString());
				entry.setDescription(description);
				
				if (entry.getContents() == null) {
					entry.setContents(new ArrayList<>(1));
				}
				SyndContent content = new SyndContentImpl();
				content.setValue(alert.getDetails().toString() + " (this is content)");
				content.setType("text/plain");
				entry.getContents().add(content);
			}
			return entry;
		}).collect(Collectors.toList()));

		return feed;
	}

	private AlertingService getAlertingService() {
		return getBean(AlertingService.class);
	}

	// FIXME better algoritm for locale selection based on Accept-Language header
	static Locale readLocaleFromAcceptLangHeader(String acceptLang) {
		Locale locale = null;
		if (acceptLang != null && !acceptLang.trim().isEmpty()) {

			String[] langs = acceptLang.split(",");
			outerLoop:
			for (String lang : langs) {			
				for (Locale loc : Locale.getAvailableLocales()) {
					if (lang.startsWith(loc.getLanguage())) {
						locale = loc;
						if (StringUtils.isNotBlank(locale.getCountry())) {
							break outerLoop;
						}
					}
				}
			}
		}
		if (locale == null) {
			LOG.warn("No locale selected based on Accept-Language header: '{}'. Using ROOT locale.", acceptLang);
			locale = Locale.ROOT;
		}
		return locale;
	}
}
