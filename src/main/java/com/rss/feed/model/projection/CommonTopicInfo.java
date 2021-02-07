package com.rss.feed.model.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CommonTopicInfo {
	
	@Value("#{target.name}")
	String getName();
	
	@Value("#{target.occureances}")
	String getOccureances();

}
