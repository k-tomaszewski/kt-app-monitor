package kt.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Time zone data from https://timezonedb.com/download
 */
public class Locales {
	
	private static final Logger LOG = LoggerFactory.getLogger(Locales.class);
	private static final String DEF_TIMEZONES_RES = "default_zone.csv";
	static final String DEFAULT_LANG_CODE = "pl";
	
	/**
	 * Mapping of default countries for known multi-country languages. Mapping uses 2-letter language codes and 2-letter country codes.
	 */
	static final Map<String, String> LANG_TO_DEF_COUNTRY = new HashMap<>();
	
	/**
	 * Mapping from a 2-lettery country/area code to an arbitrarily chosen time zone.
	 */
	static final Map<String, String> COUNTRY_TO_DEF_TZ = new HashMap<>();
	
	static {
		LANG_TO_DEF_COUNTRY.put("en", "GB");
		LANG_TO_DEF_COUNTRY.put("fr", "FR");
		LANG_TO_DEF_COUNTRY.put("de", "DE");
		LANG_TO_DEF_COUNTRY.put("zh", "TW");
		LANG_TO_DEF_COUNTRY.put("nl", "NL");
		LANG_TO_DEF_COUNTRY.put("it", "IT");
		LANG_TO_DEF_COUNTRY.put("el", "GR");
		LANG_TO_DEF_COUNTRY.put("pt", "PT");
		LANG_TO_DEF_COUNTRY.put("sr", "RS");
		LANG_TO_DEF_COUNTRY.put("es", "ES");
		
		Scanner defTzInput = new Scanner(Locales.class.getClassLoader().getResourceAsStream(DEF_TIMEZONES_RES));
		while (defTzInput.hasNextLine()) {
			final String line = defTzInput.nextLine();
			if (StringUtils.isNotBlank(line)) {
				String fields[] = line.split(",");
				if (COUNTRY_TO_DEF_TZ.put(fields[0], fields[1]) != null) {
					throw new IllegalStateException(DEF_TIMEZONES_RES + " contains more than 1 mapping for " + fields[0]);
				}
			}
		}
		defTzInput.close();
	}
	
	private Locales() {
	}
	
	/**
	 * A simple algoritm trying to select a {@link java.util.Locale} with a country based on Accept-Language HTTP header value.
	 * It's far from being perfect and uses arbitrary choices.
	 * @param acceptLang Accept-language header value
	 */
	public static Locale selectLocaleByAcceptLanguage(String acceptLang) {
		
		if (StringUtils.isBlank(acceptLang) || "*".equals(acceptLang)) {
			acceptLang = DEFAULT_LANG_CODE;
		}
		
		Locale locale = null;
		if (acceptLang != null && !acceptLang.trim().isEmpty()) {

			String[] langs = acceptLang.split("\\s*,\\s*");
			outerLoop:
			for (String lang : langs) {
				locale = Locale.forLanguageTag(lang);
				if (StringUtils.isNotBlank(locale.getCountry())) {
					break;
				}
				
				if (lang.length() == 2 && LANG_TO_DEF_COUNTRY.containsKey(lang)) {
					locale = Locale.forLanguageTag(lang + "-" + LANG_TO_DEF_COUNTRY.get(lang));
					if (StringUtils.isNotBlank(locale.getCountry())) {
						break;
					}					
				}
				
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
	
	public static DateTimeZone getDefaultTimeZoneFor(Locale locale) {
		final String tzName = COUNTRY_TO_DEF_TZ.get(locale.getCountry());
		if (tzName != null) {
			try {
				return DateTimeZone.forID(tzName);
			} catch (IllegalArgumentException e) {
				LOG.warn("Cannot select time zone by id. " + e.getMessage());
			}
		}
		LOG.warn("Using UTC time zone for locale '{}-{}'", locale.getLanguage(), locale.getCountry());
		return DateTimeZone.UTC;
	}	
}
