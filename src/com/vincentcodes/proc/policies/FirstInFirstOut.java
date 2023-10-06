package com.vincentcodes.proc.policies;

import com.vincentcodes.proc.ProcessStates;
import com.vincentcodes.proc.Processor;

public class FirstInFirstOut extends OneQueuePolicy {

    @Override
    public void work() {
        if(runningProc == null){
            // simulating context switch by pulling proc from ready queue
            runningProc = readyQueue.poll();
            if(runningProc == null) return;
            runningProc.setCurrentState(ProcessStates.RUNNING);
        }

        runningProc.work();
    }

    @Override
    public void handleNewStates(){
        if(runningProc == null) return;

        boolean canPollNewProc = false;
        if(runningProc.getRequestedState() == ProcessStates.TERM){
            canPollNewProc = true;
            Processor.LOGGER.info(runningProc.getName() + " finished at " + (Processor.CURRENT_TICK+1));
        }else if(runningProc.getRequestedState() == ProcessStates.BLOCKING){
            runningProc.setCurrentState(ProcessStates.BLOCKING);
            blockingProc.add(runningProc);
            canPollNewProc = true;
        }

        if(canPollNewProc){
            Processor.LOGGER.debug(readyQueue.toString() + blockingProc.toString());
            runningProc = null;
        }
    }
    
}
