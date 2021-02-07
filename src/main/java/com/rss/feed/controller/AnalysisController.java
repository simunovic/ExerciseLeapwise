package com.rss.feed.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.payload.request.AnalysisRequest;
import com.rss.feed.service.IAnalysisService;
import com.sun.syndication.io.FeedException;

@RestController
@RequestMapping(path = { "/analyse" })
public class AnalysisController {

	private final IAnalysisService analysisService;

	public AnalysisController(IAnalysisService analysisService) {
		this.analysisService = analysisService;
	}

	/**
	 * Method is used for extracting hot topics from given RSS feeds.
	 * 
	 * @param analysis Request model which provide list of RSS feed URL's to analyse
	 * @return Global unique identifier that uniquely represent analysisF
	 * @throws MalformedURLException If either no legal protocol could be found in a
	 *                               specified URL or URL could not be parsed
	 * @throws FeedException         If given feed can not be parsed or generated
	 * @throws IOException           If failed or interrupted I/O operation occurs
	 * @throws Exception             If there are no feed entries to analyse
	 */
	@LogEntryExit(showArgs = true, showResult = true)
	@PostMapping("/new")
	public ResponseEntity<?> newAnalysis(@Valid @RequestBody AnalysisRequest analysis)
			throws MalformedURLException, FeedException, IOException, Exception {
		return analysisService.analyse(analysis.getUrls());
	}

}
