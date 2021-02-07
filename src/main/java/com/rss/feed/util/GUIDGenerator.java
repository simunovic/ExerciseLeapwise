package com.rss.feed.util;

import java.util.UUID;

public class GUIDGenerator {
	
	/**
	 * Method is used for generating unique global identifier
	 * 
	 * @return String representation of unique global identifier specified on exactly 32 characters
	 */
	public static String generateRandomID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
