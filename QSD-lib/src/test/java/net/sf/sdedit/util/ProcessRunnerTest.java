package net.sf.sdedit.util;

import java.io.File;

public class ProcessRunnerTest {
	
	public static void main (String [] argv) throws Exception {
		File tmp = File.createTempFile("ProcessRunnerTest", ".sh");
		PWriter writer = PWriter.forFile(tmp);
		writer.println("sleep 1");
		writer.println("echo Howdy, world");
		writer.println("sleep 1");
		writer.println("echo Howdy, stderr >& 2");
		writer.println("echo " + tmp.getAbsolutePath());
		writer.println("ps >& 2");
		writer.close();
		ProcessRunner runner = new ProcessRunner();
		runner.command("bash", tmp.getAbsolutePath());
		runner.outToString().errToString();
		runner.start(0);
		System.out.println(runner.getOut());
		System.out.println(runner.getErr());
		tmp.delete();
	}

}
