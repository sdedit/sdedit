package net.sf.sdedit.text;

import static net.sf.sdedit.text.PartialPattern.COLON;
import static net.sf.sdedit.text.PartialPattern.DOT;
import static net.sf.sdedit.text.PartialPattern.EQ;
import static net.sf.sdedit.text.PartialPattern.LEVELS;
import static net.sf.sdedit.text.PartialPattern.PREFIX;
import static net.sf.sdedit.text.PartialPattern.SPAWN;

import java.util.regex.Pattern;

enum MessageType {

	LEVEL_THREAD_ANSWER(new Object[] { PREFIX, LEVELS, COLON, "(.*)", EQ, "(.+?)", DOT, "(.*)" }, "noteId", "dummy",
			"caller", "dummy", "levelString", "threadString", "levelString", "answer", "callee", "message"),

	LEVEL_THREAD_NO_ANSWER(new Object[] { PREFIX, LEVELS, COLON, "(.+?)", DOT, "(.*)" }, "noteId", "dummy", "caller",
			"dummy", "levelString", "threadString", "levelString", "callee", "message"),

	NO_LEVEL_NO_THREAD_ANSWER(new Object[] { PREFIX, COLON, "(.*)", EQ, "(.+?)", DOT, "(.*)" }, "noteId", "dummy",
			"caller", "answer", "callee", "message"),

	NO_LEVEL_NO_THREAD_NO_ANSWER(new Object[] { PREFIX, COLON, "(.*?)", DOT, "(.*)" }, "noteId", "dummy", "caller",
			"callee", "message"),

	PRIMITIVE_WITH_LEVEL(new Object[] { PREFIX, LEVELS, COLON, "(.*)" }, "noteId", "dummy", "caller", "dummy",
			"levelString", "threadString", "levelString", "message"),

	PRIMITIVE_WITHOUT_LEVEL(new Object[] { PREFIX, COLON, "(.*)" }, "noteId", "dummy", "caller", "message"),

	SPAWN_WITH_LEVEL(new Object[] { PREFIX, LEVELS, SPAWN, "(.+?)", DOT, "(.*)" }, "noteId", "dummy", "spawner",
			"dummy", "levelString", "threadString", "levelString", "callee", "message"),

	SPAWN_WITHOUT_LEVEL(new Object[] { PREFIX, SPAWN, "(.+?)", DOT, "(.*)" }, "noteId", "dummy", "spawner",
			"callee", "message");

	private String[] properties;
	
	private Pattern pattern;

	MessageType(Object[] patterns, String... properties) {
		String pattern = "";
		for (Object patternObj : patterns) {
			pattern += String.valueOf(patternObj);
		}
		this.pattern = Pattern.compile(pattern);
		this.properties = properties;
	}
	
	Pattern getPattern () {
		return pattern;
	}


	String[] getProperties() {
		return properties;
	}

}
