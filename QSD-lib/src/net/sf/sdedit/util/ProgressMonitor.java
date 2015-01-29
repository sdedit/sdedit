package net.sf.sdedit.util;

public interface ProgressMonitor {
	
	public void setProgress (ProgressMonitorable source, int total, int done);

}
