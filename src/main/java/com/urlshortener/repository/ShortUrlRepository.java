package com.urlshortener.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Repository;

import com.urlshortener.data.ShortUrl;



import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ShortUrlRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlRepository.class);

	@Value("${shorturl.user}")
	String shortUrlUser;

	private Map<String, String> keyValue = new HashMap<String, String>();

	public String save(String key, String value) {
		keyValue.put(key, value);

		return key;
	}

	public String get(String key) {

		LOGGER.info("[get]: Retrieving at {}", key);
		String value = null;
		value = keyValue.get(key);

		return value;
	}

	private ShortUrl createShortUrl(String shortId, String longUrl) {
		ShortUrl shortUrl = new ShortUrl();
		shortUrl.setShortId(shortId);
		shortUrl.setLongUrl(longUrl);
		shortUrl.setCreatedBy(shortUrlUser);
		shortUrl.setCreatedDate(Date.from(Instant.now()));
		shortUrl.setModifiedBy(shortUrlUser);
		shortUrl.setModifiedDate(Date.from(Instant.now()));
		return shortUrl;
	}

}
