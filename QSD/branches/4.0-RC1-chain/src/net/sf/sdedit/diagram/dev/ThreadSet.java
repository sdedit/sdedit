package net.sf.sdedit.diagram.dev;

import java.util.ArrayList;

public class ThreadSet {
    
    private ArrayList<SequenceThread> threads;
    
    public ThreadSet() {
        threads = new ArrayList<SequenceThread>();
    }
    
    public SequenceThread spawn () {
        return null;
    }
    
    public SequenceThread getThread (int number) {
        return threads.get(number);
    }
    
    public int getNumberOfThreads () {
        return threads.size();
    }

}
