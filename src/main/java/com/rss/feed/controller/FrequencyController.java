package com.rss.feed.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.service.IFrequencyService;

@RestController
@RequestMapping(path = { "/frequency" })
public class FrequencyController {

	private final IFrequencyService frequencyService;

	public FrequencyController(IFrequencyService frequencyService) {
		this.frequencyService = frequencyService;
	}

	/**
	 * Method is used for fetching hot topics from specified analysis
	 * 
	 * @param id Global unique identified of analysis
	 * @return Frequently mentioned topics and corresponding feed links
	 */
	@GetMapping("/{id}")
	@LogEntryExit(showArgs = true, showResult = true)
	public ResponseEntity<?> getFrequencyesById(@PathVariable String id) {
		return frequencyService.getFrequencyForGUID(id);
	}

}
