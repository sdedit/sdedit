package net.sf.sdedit.cli.test;

import net.sf.sdedit.cli.CommandLineBeanFactory;

public class TestCLI {
    
    
    public static void main (String [] argv) {
        CommandLineBeanFactory<TestOptions> factory = new CommandLineBeanFactory<TestOptions>(TestOptions.class);
        TestOptions options = factory.parse(argv);
        if (options == null) {
            factory.printHelp("test");
        } else {
            System.out.println(options);
        }
    }

}
