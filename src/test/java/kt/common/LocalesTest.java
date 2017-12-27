package kt.common;

import java.util.Arrays;
import java.util.Locale;
import kt.appmonitor.AlertingService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;

import org.junit.Test;


public class LocalesTest {

	@Test
	public void shouldDetectPlLocale() {
		// when
		Locale locale = Locales.selectLocaleByAcceptLanguage("pl,en-US;q=0.7,en;q=0.3");

		// then
		Assert.assertNotNull(locale);
		Assert.assertEquals("pl", locale.getLanguage());
		Assert.assertEquals("PL", locale.getCountry());
		Assert.assertEquals("Poland", locale.getDisplayCountry(Locale.ROOT));
	}
	
	@Test
	public void shouldDetectChLocale() {
		// when
		Locale locale = Locales.selectLocaleByAcceptLanguage("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5");
		
		// then
		Assert.assertNotNull(locale);
		Assert.assertEquals("fr", locale.getLanguage());
		Assert.assertEquals("CH", locale.getCountry());
		Assert.assertEquals("Switzerland", locale.getDisplayCountry(Locale.ROOT));	
	}
	
	@Test
	public void shouldDetecUsLocale() {
		// when
		Locale locale = Locales.selectLocaleByAcceptLanguage("en-US,en;q=0.5");
		
		// then
		Assert.assertNotNull(locale);
		Assert.assertEquals("en", locale.getLanguage());
		Assert.assertEquals("US", locale.getCountry());
	}
	
	@Test
	public void shouldDetectFrLocaleWithFranceAsCountry() {	
		// when
		Locale locale = Locales.selectLocaleByAcceptLanguage("fr");
		
		// then
		Assert.assertNotNull(locale);
		Assert.assertEquals("fr", locale.getLanguage());
		Assert.assertEquals("FR", locale.getCountry());
	}
	
	@Test
	public void shouldSelectTimeZoneForPolishLanguage() {
		// given
		Locale plLocale = Arrays.stream(Locale.getAvailableLocales()).filter(loc -> "PL".equals(loc.getCountry())).findFirst().orElse(null);
		Assert.assertNotNull(plLocale);
		Assert.assertTrue(StringUtils.isNotBlank(plLocale.getCountry()));
		
		// when
		DateTimeZone dtz = Locales.getDefaultTimeZoneFor(plLocale);
		
		// then
		Assert.assertNotNull(dtz);
		Assert.assertEquals("Europe/Warsaw", dtz.getID());
	}		
}
