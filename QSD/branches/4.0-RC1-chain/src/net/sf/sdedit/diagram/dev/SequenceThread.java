package net.sf.sdedit.diagram.dev;

import java.util.LinkedList;

import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.message.Message;

public class SequenceThread {
    
    public static final String DEAD = "dead";
    
    public static final String RUNNING = "running";
    
    private String state;
    
    private Lifeline firstObject;
    
    private LinkedList<Message> stack;
    
    private final int number;
    
    public SequenceThread (int number) {
        this.number = number;
    }
    
    public int getNumber () {
        return number;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setFirstObject(Lifeline firstObject) {
        this.firstObject = firstObject;
    }

    public Lifeline getFirstObject() {
        return firstObject;
    }
    
    public void push (Message message) {
        stack.addLast(message);
    }
    
    public Message pop () {
        return stack.getLast();
    }
    
    public Message peek () {
        return stack.getLast();
    }
    
    
    
    

}
