// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.log;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Logger {
	
	private static Logger defaultLogger = new Logger();
	
	public static abstract class LoggerContext {
		
		protected Logger newLogger () {
			return new Logger();
		}
		
		public abstract Logger getLogger();
		
	}
	
	
	public static class Event {
		
		private long time;
		
		private String message;
		
		private String type;
		
		public Event (String message, String type) {
			this.message = message;
			this.type = type;
			time = System.currentTimeMillis();
		}
		
		public int getDifference (Event prior) {
			return (int) (prior.time - time);
		}
		
		public Calendar getTime() {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			return cal;
		}
		
		public String getType () {
			return type;
		}
		
		public String getMessage () {
			return message;
		}
		
	}
	
	private static LoggerContext context;
	
	public static void setContext (LoggerContext context) {
		Logger.context = context;
	}
	
	public static Logger getLogger () {
		if (context != null) {
			return context.getLogger();
		}
		return defaultLogger;
	}
	
	private List<Event> events;
	
	protected Logger () {
		
		events = new LinkedList<Event>();
		
	}
	
	public void log (String message) {
		log(message, "default");
	}
	
	public void log (String message, String type) {
		events.add(new Event(message, type));
		
	}
	
	public List<Event> getEvents () {
		return events;
	}
	
	public List<Event> getEvents (String filter) {
		List<Event> filtered = new LinkedList<Event>();
		for (Event event : events) {
			if (Pattern.matches(filter, event.getType())) {
				filtered.add(event);
			}
		}
		return filtered;
	}

}
