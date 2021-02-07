package com.rss.feed.payload.request;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

public class AnalysisRequest {

	@Size(min = 2, message = "At least two RSS feed URLs should be given.")
	private List<String> urls;

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	@Override
	public String toString() {
		return this.urls.stream().collect(Collectors.joining(", "));
	}

}
