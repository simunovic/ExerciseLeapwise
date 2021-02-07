package com.rss.feed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rss.feed.logging.annotation.LogEntryExit;
import com.rss.feed.model.Topic;
import com.rss.feed.model.projection.CommonTopicInfo;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

	/**
	 * Method is used for calculating and fetching common topics.
	 * 
	 * @param numberOfRows Number of hot topics to be fetched
	 * @param executionId  Global unique identifier of specific analysis
	 * @return List of Object's that represents most frequently discussed topics
	 *         from specific analysis
	 */
	@Query(value = "SELECT TOP ?1 t.name, count(*) as occureances FROM feed AS f JOIN topic AS t on f.id = t.feed_id where f.execution_id=?2 GROUP BY t.name ORDER BY COUNT(*) desc", nativeQuery = true)
	@LogEntryExit(showArgs = true)
	List<CommonTopicInfo> findCommonTopics(String numberOfRows, String executionId);

}
