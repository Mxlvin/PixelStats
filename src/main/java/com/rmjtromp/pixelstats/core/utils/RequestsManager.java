package com.rmjtromp.pixelstats.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public final class RequestsManager {
	
	private Timer timer = null;
	private final List<Request> requests = new ArrayList<>();

	public enum RequestStage { WAITING, REQUESTED, COMPLETE }	
	public enum RequestResponse { SUCCESSFUL, FAILED }

	public RequestsManager() {
		start();
	}
	
	public void start() {
		if(timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					requests.removeAll(requests.stream().filter(r -> {
						// remove "WAITING" requests that are more than 5 minutes old
						if(r.stage.equals(RequestStage.WAITING)) return r.time < System.currentTimeMillis() - 300000;
						// remove other requests if they're older than 1 minute
						else return r.time < System.currentTimeMillis() - 60000;
					}).collect(Collectors.toList()));
				}
			}, 1000, 1000);
		}
	}
	
	public boolean isRunning() {
		return timer != null;
	}
	
	public void stop() {
		if(timer != null) {
			timer.cancel();
			timer = null;
			requests.clear();
		}
	}
	
	public Request createRequest() {
		Request request = new Request();
		requests.add(request);
		return request;
	}
	
	public int getTotalRPM() {
		return requests.size();
	}
	
	public int getSuccessfulRPM() {
		return (int) requests.stream().filter(r -> r.stage.equals(RequestStage.COMPLETE) && r.response.equals(RequestResponse.SUCCESSFUL)).count();
	}
	
	public int getFailedRPM() {
		return (int) requests.stream().filter(r -> r.stage.equals(RequestStage.COMPLETE) && r.response.equals(RequestResponse.FAILED)).count();
	}
	
	public static class Request {
		
		private RequestStage stage = RequestStage.WAITING;
		private long time = System.currentTimeMillis();
		private RequestResponse response = null;
		
		private Request() {}
		
		public void setStage(RequestStage stage) {
			if(stage != null && !this.stage.equals(stage)) {
				this.stage = stage;
				if(this.stage.equals(RequestStage.REQUESTED)) time = System.currentTimeMillis();
			}
		}
		
		public void setResponse(RequestResponse response) {
			if(response != null && this.response == null) {
				this.stage = RequestStage.COMPLETE;
				this.response = response;
			}
		}
		
	}
	
}
