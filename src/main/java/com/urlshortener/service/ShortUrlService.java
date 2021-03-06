package com.urlshortener.service;

import com.google.common.hash.Hashing;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.utilities.ShortUrlConstants;
import com.urlshortener.utilities.ShortUrlException;
import com.urlshortener.utilities.ShortUrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;


@Service
public class ShortUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlService.class);
    @Autowired
    ShortUrlRepository shortUrlRepository;

    public String createShortUrl(String requestUrl,String longUrl) {
        LOGGER.info("[createShortUrl]: creating short url for: {}" , longUrl);
        if(requestUrl == null || requestUrl.isEmpty() || longUrl == null || longUrl.isEmpty() ){
            LOGGER.error("[createShortUrl]: {}",ShortUrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL_MESSAGE + requestUrl + longUrl);
            throw new ShortUrlException(ShortUrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL_MESSAGE + requestUrl + longUrl);
        }
        String shortHash = Hashing.murmur3_32().hashString(longUrl, StandardCharsets.UTF_8).toString();
        String shortId = shortUrlRepository.save(shortHash, ShortUrlUtil.urlEncode(longUrl));
        String shortUrl  = ShortUrlUtil.BuildShortUrl(requestUrl,shortId);
        LOGGER.info("[createShortUrl]: created short url is: {}" , shortUrl);
        return shortUrl;
    }
     public String getLongUrl(String shortId) {
         if(shortId == null || shortId.isEmpty()) {
             LOGGER.error("[getLongUrl]: short id is null or empty");
             throw new ShortUrlException(ShortUrlConstants.NULL_EMPTY_SHORT_ID_MESSAGE + shortId);
         }
        LOGGER.info("[getLongUrl]: getting url for shortId: {}" , shortId);
        String longurl = shortUrlRepository.get(shortId);
        if(longurl == null || longurl.isEmpty()) {
            LOGGER.error("[getLongUrl]: long url is null or empty");
            throw new ShortUrlException(ShortUrlConstants.INVALID_URL_MESSAGE + shortId);
        }
       else {
           String longUrl = ShortUrlUtil.urlDecode(longurl);
           LOGGER.info("[getLongUrl]: long url is: {}" , longUrl);
            return longUrl;
        }
    }
}
