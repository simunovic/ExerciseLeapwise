package com.rss.feed.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sun.syndication.io.FeedException;

public interface IAnalysisService {

	public ResponseEntity<?> analyse(List<String> urls)
			throws MalformedURLException, FeedException, IOException, Exception;

}
