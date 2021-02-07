package com.rss.feed.service;

import org.springframework.http.ResponseEntity;

public interface IFrequencyService {
	
	public ResponseEntity<?> getFrequencyForGUID(String guid);

}
