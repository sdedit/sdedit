package net.sf.sdedit.cli.test;

import net.sf.sdedit.cli.IOptions;
import net.sf.sdedit.cli.Option;

public interface TestOptions extends IOptions {
    
    @Option(group="x",required=true)
    public String getA();
    
    @Option(group="x",required=false)
    public String getB();
    
    @Option(group="x",required=false)
    public String getC();

}
