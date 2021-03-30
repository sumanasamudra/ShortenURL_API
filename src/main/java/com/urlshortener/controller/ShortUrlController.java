package com.urlshortener.controller;

import com.urlshortener.data.UrlRequestResponse;
import com.urlshortener.service.ShortUrlService;
import com.urlshortener.utilities.ShortUrlConstants;
import com.urlshortener.utilities.ShortUrlUtil;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;


@RestController
public class ShortUrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlController.class);
    @Autowired
    ShortUrlService shortUrlService;

   

    @GetMapping("/{id}")
    public ResponseEntity redirect(@PathVariable String id) {
        try {
            if (id == null || id.isEmpty()) {
                
                LOGGER.error("[redirect]: id is empty or null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE + id);
            }
            LOGGER.info("[redirect]: Received short url for redirect with id: {}", id);
            String longUrl = shortUrlService.getLongUrl(id);
            if (longUrl == null || longUrl.isEmpty()) {
                
                LOGGER.error("[redirect]: long url is empty or null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
            } else {
               
                LOGGER.info("[redirect]: Performing redirect to url: {} " , longUrl);
				
                return new ResponseEntity<>(new UrlRequestResponse(longUrl) , HttpStatus.OK);
            }
        } catch (Exception ex) {
            
            LOGGER.error("[redirect]: {}",ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PostMapping(value = "/shorturl", consumes = {"application/json"})
    public @ResponseBody
    ResponseEntity<UrlRequestResponse> createShortUrl(@RequestBody UrlRequestResponse urlRequest, HttpServletRequest request) {
        if (urlRequest == null || urlRequest.getUrl() == null || urlRequest.getUrl().isEmpty()) {
            
            LOGGER.error("[createShortUrl]: {}",ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
        }
        LOGGER.info("[createShortUrl]: Received input for url shortening: {}" , urlRequest.getUrl());
        if (!ShortUrlUtil.isUrlValid(urlRequest.getUrl())) {
           
            LOGGER.error("[createShortUrl]: {}",ShortUrlConstants.INVALID_URL_MESSAGE + urlRequest.getUrl());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.INVALID_URL_MESSAGE);
        }
        try {
            String shortUrl = shortUrlService.createShortUrl(getBaseUrl(request), urlRequest.getUrl());
            
            LOGGER.info("[createShortUrl]: Shortened url: {}" , shortUrl);
            return new ResponseEntity<>(new UrlRequestResponse(shortUrl) , HttpStatus.OK);
        } catch (Exception ex) {
           
            LOGGER.error("[createShortUrl]: {}",ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort() + "/";
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }
}
