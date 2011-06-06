package net.sf.sdedit.diagram.dev;

import net.sf.sdedit.diagram.MessageData;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SyntaxError;
import net.sf.sdedit.util.Ref;

public class IdentifyThreads extends SequenceProcessor<MessageData> {

    protected IdentifyThreads(SequenceProcessorChain chain) {
        super(chain);
    }

    @Override
    protected Class<MessageData> getElementClass() {
        return MessageData.class;
    }

    @Override
    public void processElement() throws SemanticError,
            SyntaxError {
        if (getDiagram().isThreaded()) {
            Ref<SequenceThread> callerThread = new Ref<SequenceThread>();
            Ref<SequenceThread> calleeThread = new Ref<SequenceThread>();
            ThreadSet threads = getDiagram().getThreadSet();
            State state = getState();
            MessageData data = getElement();
            if ("$".equals(data.getCaller())) {
                callerThread.t = null;
                calleeThread.t = threads.spawn();
            } else {
                if (state.callerIsActor() && (state.isPrimitiveMessage() || state.calleeIsActor())) {
                    callerThread.t = null;
                    calleeThread.t = null;
                } else {
                    if (state.callerIsActor()) {
                        callerThread.t = null;
                        if (!data.returnsInstantly()) {
                            calleeThread.t = threads.spawn();
                        }
                    } else {
                        callerIsNotAnActor(callerThread, calleeThread);
                    }
                }
            }
        }
       callNext();
    }

    private void callerIsNotAnActor(Ref<SequenceThread> callerThread, Ref<SequenceThread> calleeThread) throws SemanticError, SyntaxError {
        State state = getState();
        ThreadSet threadSet = getDiagram().getThreadSet();
        MessageData data = getElement();
        if (threadSet.getNumberOfThreads() == 0) {
            callerThread.t = threadSet.spawn();
        } else {
          /*
          * If the MessageData specifies a thread number, we'll take it
          * as the current thread number, otherwise we check if the
          * caller object (represented by callerRoot) is just by only a
          * single thread.
          */
            if (data.getThread()>=0) {
                // mnemonic!?
                if (data.getThread() >= threadSet.getNumberOfThreads()) {
                    semanticError("Illegal thread number: " + data.getThread());
                }
                callerThread.t = threadSet.getThread(data.getThread());
            } else {
                // TODO replace thread numbers be thread objects
                final int uniqueThread = state.getRootCaller().getUniqueThread();
                if (uniqueThread >= 0) {
                    callerThread.t = threadSet.getThread(uniqueThread);
                } else {
                    if (calleeThread == null) {
                        semanticError("Explicit thread number required");
                    }
                    callerThread.t = calleeThread.t;
                }
            }
            
        }
        
    }
    

    
//    /**
//     * 
//     * @pre threading is used
//     * @post callerThread and calleeThread are set
//     * 
//     * @throws SemanticError
//     */
//    private void findThreadNumbers() throws SemanticError {
//
//        if (data.getCaller().equals("$")) {
//            callerThread = -1;
//            calleeThread = diagram.spawnThread();
//            return;
//        }
//
//        if (callerIsActor() && (isPrimitiveMessage() || calleeIsActor())) {
//            callerThread = -1;
//            calleeThread = -1;
//            return;
//        }
//        if (callerIsActor()) {
//            callerThread = -1;
//            if (!data.returnsInstantly()) {
//                calleeThread = diagram.spawnThread();
//            }
//            return;
//        }
//
//        // The caller is not an actor.
//
//        if (diagram.noThreadIsSpawned()) {
//            callerThread = diagram.spawnThread();
//        } else {
//
//            Lifeline lineToBeFound = null;
//
//            if (!data.getCallerMnemonic().equals("")) {
//                lineToBeFound = diagram.getLifelineByMnemonic(data.getCaller(),
//                        data.getCallerMnemonic());
//                if (lineToBeFound == null) {
//                    throw new SemanticError(provider,
//                            "There is no lifeline named \"" + data.getCaller()
//                                    + "\" associated to mnemonic \""
//                                    + data.getCallerMnemonic() + "\"");
//                }
//                callerThread = lineToBeFound.getThread();
//
//            } else {
//
//                /*
//                 * If the MessageData specifies a thread number, we'll take it
//                 * as the current thread number, otherwise we check if the
//                 * caller object (represented by callerRoot) is just by only a
//                 * single thread.
//                 */
//                if (data.getThread() >= 0) {
//                    if (data.getThread() >= diagram.getNumberOfThreads()) {
//                        throw new SemanticError(provider,
//                                "Illegal thread number: " + data.getThread());
//                    }
//                    callerThread = data.getThread();
//                } else {
//                    final int uniqueThread = rootCaller.getUniqueThread();
//                    if (uniqueThread >= 0) {
//                        callerThread = uniqueThread;
//                    } else {
//                        if (calleeThread == -1) {
//                            throw new SemanticError(provider,
//                                    "Explicit thread number required.");
//                        }
//                        callerThread = calleeThread;
//                    }
//                }
//            }
//        }
//
//        boolean spawning = data.isSpawnMessage();
//
//        if (spawning) {
//            if (data.getBroadcastType() > 0) {
//                spawning = !calleeIsActor();
//            }
//        }
//
//        if ((spawning || calleeIsActiveObject()) && !data.returnsInstantly()) {
//            calleeThread = diagram.spawnThread();
//        } else {
//            calleeThread = callerThread;
//        }
//
//    }

}
