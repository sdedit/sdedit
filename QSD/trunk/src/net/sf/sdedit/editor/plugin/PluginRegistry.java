package net.sf.sdedit.editor.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PluginRegistry implements Iterable<Plugin>{
	
	private static PluginRegistry instance = new PluginRegistry();
	
	public static PluginRegistry getInstance() {
		return instance;
	}
	
	private List<Plugin> plugins;
	
	private PluginRegistry () {
		plugins = new ArrayList<Plugin>();
	}
	
	public void addPlugin (Plugin plugin) {
		plugins.add(plugin);
	}
	
	public <T extends Plugin> Plugin getPlugin(Class<T> cls) {
		for (Plugin plugin : this) {
			if (cls == plugin.getClass()) {
				return plugin;
			}
		}
		return null;
	}

	public Iterator<Plugin> iterator() {
		return plugins.iterator();
	}
	
	
	
	

}
