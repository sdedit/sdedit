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
