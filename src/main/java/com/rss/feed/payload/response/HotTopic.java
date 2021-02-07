package com.rss.feed.payload.response;

import java.util.List;
import java.util.stream.Collectors;

public class HotTopic {

	private String name;
	private List<LinkedFeed> relatedFeeds;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LinkedFeed> getRelatedFeeds() {
		return relatedFeeds;
	}

	public void setRelatedFeeds(List<LinkedFeed> relatedFeeds) {
		this.relatedFeeds = relatedFeeds;
	}

	@Override
	public String toString() {
		String relatedFeeds = this.relatedFeeds.stream().map(e -> e.toString()).collect(Collectors.joining(", "));
		return new StringBuilder().append("{Name=").append(this.name).append(", relatedFeeds=").append(relatedFeeds).append("}")
				.toString();
	}

}
