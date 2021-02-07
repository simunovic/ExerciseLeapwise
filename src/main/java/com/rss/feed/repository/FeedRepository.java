package com.rss.feed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.model.Feed;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

	/**
	 * Method is used for fetching feeds that are related to hot topic.
	 * 
	 * @param executionId     Global unique identifier of specific analysis
	 * @param commonTopicName Hot topic name
	 * @return List of feeds related to hot topic and execution id
	 */
	@Query(value = "select f.* from feed as f join topic as t on f.id = t.feed_id where f.execution_id = ?1 and t.name = ?2", nativeQuery = true)
	@LogEntryExit(showArgs = true)
	List<Feed> findFeedsByExecutionIdAndCommonTopicName(String executionId, String commonTopicName);

}
