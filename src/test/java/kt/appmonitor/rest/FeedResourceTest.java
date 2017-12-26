package kt.appmonitor.rest;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;

import org.junit.Test;


public class FeedResourceTest {

	@Test
	public void shouldDetectLocaleBasedOnAcceptLanguageHeader() {
		// when
		Locale detectedLocale = FeedResource.readLocaleFromAcceptLangHeader("pl,en-US;q=0.7,en;q=0.3");

		// then
		Assert.assertNotNull(detectedLocale);
		Assert.assertEquals("pl", detectedLocale.getLanguage());
		Assert.assertTrue("Country name from Locale object cannot be empty.", StringUtils.isNotBlank(detectedLocale.getCountry()));
	}
	
	@Test
	public void shouldSelectTimeZoneForPolishLanguage() {
		// given
		Locale plLoc = FeedResource.readLocaleFromAcceptLangHeader("pl");
		
		// when
		DateTimeZone dtz = FeedResource.getTimeZoneForLocale(plLoc);
		
		// then
		Assert.assertNotNull(dtz);
		System.out.println("Selected time zone: " + dtz);
	}
}
