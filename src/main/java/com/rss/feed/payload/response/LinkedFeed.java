package com.rss.feed.payload.response;

public class LinkedFeed {

	private String header;
	private String link;
	
	public LinkedFeed() {
		
	}
	
	public LinkedFeed(String header, String link) {
		this.header = header;
		this.link = link;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("{Header=").append(this.header).append(", link=").append(this.link).append("}").toString();
	}

}
