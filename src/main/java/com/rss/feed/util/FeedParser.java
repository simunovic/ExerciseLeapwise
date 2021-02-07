package com.rss.feed.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Component
public class FeedParser {

	private final List<String> stopwords;

	private final List<String> newspapers;

	public FeedParser(@Value("${hottopic.constants.stopwords}") String stopwords,
			@Value("${hottopic.constants.newspapers}") String newspapers) {
		this.stopwords = Stream.of(stopwords.toLowerCase().split(",")).collect(Collectors.toList());
		this.newspapers = Stream.of(newspapers.toLowerCase().split(",")).collect(Collectors.toList());
	}

	/**
	 * Method is used for parsing RSS feed into list of feed entries.
	 * 
	 * @param url RSS feed URL which need to be parsed
	 * @return List of feed entries
	 * @throws MalformedURLException If either no legal protocol could be found in a
	 *                               specified URL or URL could not be parsed
	 * @throws FeedException         If given feed can not be parsed or generated
	 * @throws IOException           If failed or interrupted I/O operation occurs
	 */
	@LogEntryExit(showArgs = true)
	public List<SyndEntry> readRSSFeed(String url) throws MalformedURLException, FeedException, IOException {
		URL feedSource = new URL(url);
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));
		return validateFeedEntries(feed.getEntries());
	}

	/**
	 * Method is used for validating feed entries returned by ROME library
	 * getEntries method since it returns raw List
	 * 
	 * @param feedEntries Raw List of feed entries returned by ROME library
	 * @return List of SyndEntry objects representing feed entries
	 */
	private List<SyndEntry> validateFeedEntries(List<?> feedEntries) {
		List<SyndEntry> validatedEntries = new ArrayList<>();

		feedEntries.forEach(e -> {
			if (e instanceof SyndEntry) {
				validatedEntries.add((SyndEntry) e);
			}
		});

		return validatedEntries;
	}

	/**
	 * Method is used for parsing feed entries into list of feed topics extracted
	 * from feed title.
	 * 
	 * @param feed Feed entry whose topics are being extracted from title
	 * @return List of feed entry related topics
	 */
	public List<String> getEntryTopics(SyndEntry feed) {

		List<String> separatedWordsFromTitle = new ArrayList<>();

		// Get RSS feed title in lower case
		StringBuilder titleBuilder = new StringBuilder(feed.getTitle().toLowerCase());

		// Removing newspapers from title
		newspapers.forEach(news -> {
			int i = titleBuilder.toString().indexOf(news);
			if (i != -1) {
				titleBuilder.delete(i, i + news.length());
			}
		});

		// Remove special characters and split title words
		Collections.addAll(separatedWordsFromTitle, titleBuilder.toString().replaceAll("[^a-z0-9 ]", "").split("[ ]+"));

		// Removing stopwords
		separatedWordsFromTitle.removeAll(stopwords);

		// Select distinct and remove blank words
		return separatedWordsFromTitle.stream().distinct().filter(word -> !StringUtils.isBlank(word))
				.collect(Collectors.toList());
	}

}
