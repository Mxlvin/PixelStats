package com.rmjtromp.pixelstats.core.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public final class HTTPRequest {

	public static HttpURLConnection get(String targetURL) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

}
