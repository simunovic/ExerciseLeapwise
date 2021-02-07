package com.rss.feed.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.model.Feed;
import com.rss.feed.model.Topic;
import com.rss.feed.repository.FeedRepository;
import com.rss.feed.service.IAnalysisService;
import com.rss.feed.util.FeedParser;
import com.rss.feed.util.GUIDGenerator;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;

@Service
public class AnalysisServiceImpl implements IAnalysisService {

	private final FeedParser feedParser;
	private final FeedRepository feedRepository;

	public AnalysisServiceImpl(FeedParser feedParser, FeedRepository feedRepository) {
		this.feedParser = feedParser;
		this.feedRepository = feedRepository;
	}

	/**
	 * Method is used for extracting relevant words from topic title and managing
	 * data storing into DB
	 * 
	 * @param urls List of RSS feed URL's that needs to be analysed
	 * @return Global unique identifier that uniquely represent analysis
	 * @throws MalformedURLException If either no legal protocol could be found in a
	 *                               specified URL or URL could not be parsed
	 * @throws FeedException         If given feed can not be parsed or generated
	 * @throws IOException           If failed or interrupted I/O operation occurs
	 * @throws Exception             If there are no feed entries to analyse
	 */
	@Override
	@LogEntryExit(showArgs = true, showResult = true)
	public ResponseEntity<?> analyse(List<String> urls)
			throws MalformedURLException, FeedException, IOException, Exception {

		// Generate unique identifier for execution
		String guid = GUIDGenerator.generateRandomID();

		for (String url : urls) {

			// Fetch feed entries by URL
			List<SyndEntry> feedEntries = feedParser.readRSSFeed(url);

			// validate feed entries
			validateFeedEntry(feedEntries, url);

			// Get entry topics and save them into DB
			feedEntries.forEach(entry -> {
				Feed feed = new Feed();
				feed.setExecution_id(guid);
				feed.setTitle(entry.getTitle());
				feed.setLink(entry.getLink());

				List<Topic> topics = feedParser.getEntryTopics(entry).stream().map(e -> convertToTopic(e, feed))
						.collect(Collectors.toList());

				if (topics != null && !topics.isEmpty()) {
					feed.setTopics(topics);
				}

				feedRepository.save(feed);
			});

		}

		return ResponseEntity.ok(guid);
	}

	/**
	 * Method is used for validating list of feed entries
	 * 
	 * @param feedEntries List of feed entries that needs to be validated
	 * @param url         URL from which feeds are fetched
	 * @throws FeedException If given feed can not be parsed or generated
	 * @throws Exception     If there are no feed entries to analyse
	 */
	private void validateFeedEntry(List<SyndEntry> feedEntries, String url) throws FeedException, Exception {
		if (feedEntries == null) {
			throw new FeedException(
					String.format("Something went wrong trying to fetch feeds. Check if feed URL is correct: {}", url));
		}

		if (feedEntries.isEmpty()) {
			throw new Exception(String.format("There are no feed entries to analyse: {}", url));
		}
	}

	/**
	 * Method is used for const4ructing new Topic object
	 * 
	 * @param element Topic name
	 * @param feed    Feed correlated to topic
	 * @return New Topic object
	 */
	public Topic convertToTopic(String element, Feed feed) {
		return new Topic(element, feed);
	}

}
