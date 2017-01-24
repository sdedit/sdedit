package net.sf.sdedit.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Multiplicator {

	private List<Pair<String, Iterable<Object>>> drivers;

	public Multiplicator() {
		drivers = new ArrayList<Pair<String, Iterable<Object>>>();
	}

	private void multiply(int idx, Map<String, Object> template, List<Map<String, Object>> list) {
		Iterable<Object> driver = drivers.get(idx).getSecond();
		String name = drivers.get(idx).getFirst();
		for (Object obj : driver) {
			template = new LinkedHashMap<String, Object>(template);
			template.put(name, obj);
			if (idx == drivers.size() - 1) {
				list.add(template);
			} else {
				multiply(idx + 1, template, list);
			}
		}
	}

	public List<Map<String, Object>> getTuples() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (drivers.size() > 0) {
			multiply(0, new LinkedHashMap<String, Object>(), list);	
		}
		return list;
	}

	public void addDriver(String name, Iterable<Object> driver) {
		drivers.add(Utilities.pair(name, driver));
	}
	

}
