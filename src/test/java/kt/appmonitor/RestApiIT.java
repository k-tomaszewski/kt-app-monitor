package kt.appmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TreeMap;
import javax.ws.rs.core.MediaType;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AppHeartBeatDto;
import kt.common.jackson.DateTimeModule;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


public class RestApiIT {
	static final Logger LOG = LoggerFactory.getLogger(RestApiIT.class);
	static final String BASIC_URL = "http://localhost:8080/api/v1/";
	static final String APP_NAME_PREFIX = "test-" + System.currentTimeMillis() + '-';
	static int counter = 0;	
	
	final RestTemplate restTemplate = new RestTemplate();
	final HttpHeaders headers = new HttpHeaders();

	
	public RestApiIT() {
		restTemplate.getMessageConverters().stream().filter(mc -> mc instanceof MappingJackson2HttpMessageConverter).findFirst()
			.ifPresent((httpMsgConverter) -> {
				MappingJackson2HttpMessageConverter jacksonMsgConverter = (MappingJackson2HttpMessageConverter) httpMsgConverter;
				LOG.debug("MappingJackson2HttpMessageConverter found in RestTemlate message converters. Registering {}...", DateTimeModule.class);
				jacksonMsgConverter.getObjectMapper().registerModule(new DateTimeModule());
			});
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse chr) throws IOException {
				return chr.getRawStatusCode() / 100 != 2;
			}

			@Override
			public void handleError(ClientHttpResponse chr) throws IOException {
				LOG.error("Response: {} {}. Headers: {}. Content:\n{}",
					chr.getStatusCode(), chr.getStatusText(), chr.getHeaders(), read(chr.getBody()));
			}
		});
		headers.add("Content-type", MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void shouldHandleGetRequestForAnyAppName() {
		// given
		final String appName = APP_NAME_PREFIX + (++counter);
		LOG.info("Using app-name: {}", appName);
		
		// when
		AppAliveEntry[] appAliveEntries = restTemplate.getForObject(BASIC_URL + appName, AppAliveEntry[].class);
		
		// then
		Assert.assertNotNull(appAliveEntries);
		Assert.assertEquals(0, appAliveEntries.length);
	}

	@Test
	public void shouldAcceptPutRequestWithHeartBeatOnly() throws URISyntaxException {
		// given
		AppHeartBeatDto heartBeat = new AppHeartBeatDto();
		heartBeat.setTimestamp(DateTime.now());
		
		final String appName = APP_NAME_PREFIX + (++counter);
		LOG.info("Using app-name: {}", appName);
		
		// when
		RequestEntity<AppHeartBeatDto> request = new RequestEntity<>(heartBeat, headers, HttpMethod.PUT, new URI(BASIC_URL + appName));
		restTemplate.exchange(request, Void.class);
		AppAliveEntry[] appAliveEntries = restTemplate.getForObject(BASIC_URL + appName, AppAliveEntry[].class);
		
		// then
		Assert.assertNotNull(appAliveEntries);
		Assert.assertEquals(1, appAliveEntries.length);
		Assert.assertTrue(appAliveEntries[0].getMetricsEntries().isEmpty());
	}
	
	@Test
	public void shouldAcceptPutRequestWithHeartBeatAndMetrics() throws URISyntaxException {
		// given
		TreeMap<String, Object> metrics = new TreeMap<>();
		metrics.put("temperatura", 45.52);
		metrics.put("load avg", new float[]{0.3f, 0.2f, 0.07f});
		
		AppHeartBeatDto heartBeat = new AppHeartBeatDto();
		heartBeat.setTimestamp(DateTime.now());
		heartBeat.setMetrics(metrics);
		
		final String appName = APP_NAME_PREFIX + (++counter);
		LOG.info("Using app-name: {}", appName);
		
		// when
		RequestEntity<AppHeartBeatDto> request = new RequestEntity<>(heartBeat, headers, HttpMethod.PUT, new URI(BASIC_URL + appName));
		restTemplate.exchange(request, Void.class);
		AppAliveEntry[] appAliveEntries = restTemplate.getForObject(BASIC_URL + appName, AppAliveEntry[].class);
		
		// then
		Assert.assertNotNull(appAliveEntries);
		Assert.assertEquals(1, appAliveEntries.length);
		Assert.assertEquals(1, appAliveEntries[0].getMetricsEntries().size());
	}
	
	static String read(InputStream is) {
		StringBuilder output = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append('\n');
			}

		} catch (IOException ex) {
			LOG.error("CANNOT READ INPUT STREAM. " + ex.getMessage(), ex);
		}
		return output.toString();		
	}
}
