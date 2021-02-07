package com.rss.feed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.model.Feed;
import com.rss.feed.model.projection.CommonTopicInfo;
import com.rss.feed.payload.response.HotTopic;
import com.rss.feed.payload.response.LinkedFeed;
import com.rss.feed.repository.FeedRepository;
import com.rss.feed.repository.TopicRepository;
import com.rss.feed.service.IFrequencyService;

/**
 * In this implementation hot topic calculation is moved to DB layer for
 * simplicity and higher data granularity although there are more performance
 * focused solutions
 */
@Service
public class FrequencyServiceImpl implements IFrequencyService {

	@Value("${hottopic.result.count}")
	private String numberOfResults;

	private final TopicRepository topicRepository;
	private final FeedRepository feedRepository;

	public FrequencyServiceImpl(TopicRepository topicRepository, FeedRepository feedRepository) {
		this.topicRepository = topicRepository;
		this.feedRepository = feedRepository;
	}

	/**
	 * Method is used for fetching hot topics from DB by given unique identifier
	 * 
	 * @param guid Global unique identifier of analysis whose topics are being
	 *             fetched
	 * @return Frequently mentioned topics and corresponding feed links
	 */
	@Override
	@LogEntryExit(showArgs = true, showResult = true)
	public ResponseEntity<?> getFrequencyForGUID(String guid) {

		List<HotTopic> topResults = new ArrayList<HotTopic>();

		List<CommonTopicInfo> commonTopics = topicRepository.findCommonTopics(numberOfResults, guid);

		commonTopics.forEach(commonTopic -> {
			HotTopic hotTopic = new HotTopic();
			hotTopic.setName(commonTopic.getName());
			List<Feed> topic = feedRepository.findFeedsByExecutionIdAndCommonTopicName(guid, commonTopic.getName());
			hotTopic.setRelatedFeeds(topic.stream().map(e -> convertToLinkedFeed(e)).collect(Collectors.toList()));
			topResults.add(hotTopic);
		});

		return validateTopResults(topResults);
	}

	/**
	 * Method is used for validating if list of hot topics are empty
	 * 
	 * @param topResults HotTopics for given execution id
	 * @return Response with status OK and corresponding message if there are no
	 *         hot topics for given execution id
	 */
	private ResponseEntity<?> validateTopResults(List<HotTopic> topResults) {
		if (topResults.isEmpty()) {
			return ResponseEntity
					.ok("Service found nothing for given execution id. Check if correct execution id is provided.");
		}

		return ResponseEntity.ok(topResults);
	}

	/**
	 * Method is used for constructing new LinkedFeed object from given Feed object
	 * 
	 * @param feed Feed object that is used for constructing LinkedFeed object
	 * @return New LinkedFeed object
	 */
	private LinkedFeed convertToLinkedFeed(Feed feed) {
		return new LinkedFeed(feed.getTitle(), feed.getLink());
	}

}
