package com.vincentcodes.proc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class OneQueuePolicy implements SchedulePolicy{
    protected final Queue<ProcessContext> readyQueue;
    protected final List<ProcessContext> blockingProc;
    protected ProcessContext runningProc;

    public OneQueuePolicy(){
        readyQueue = new LinkedList<>();
        blockingProc = new ArrayList<>();
    }

    @Override
    public void tickBlockingProc(){
        // Processor.LOGGER.debug("---block");
        for(int i = blockingProc.size()-1; i >= 0; i--){
            ProcessContext proc = blockingProc.get(i);
            proc.work();
            if(proc.getCurrentState() == ProcessStates.READY){
                blockingProc.remove(i);
                readyQueue.add(proc);
            }else if(proc.getRequestedState() == ProcessStates.TERM){
                blockingProc.remove(i);
                Processor.LOGGER.info(proc.getName() + " finished at " + (Processor.CURRENT_TICK+1));
            }
        }
        // Processor.LOGGER.debug("---end block");
    }

    @Override
    public void submit(SimpleProcess proc, String name) {
        readyQueue.add(new ProcessContext(proc, Processor.CURRENT_TICK, name));
    }

    @Override
    public boolean allDone() {
        return runningProc == null && readyQueue.size() == 0 && blockingProc.size() == 0;
    }
}
