package com.vincentcodes.proc.policies;

import com.vincentcodes.proc.OneQueuePolicy;
import com.vincentcodes.proc.ProcessStates;
import com.vincentcodes.proc.Processor;

public class RoundRobin extends OneQueuePolicy {
    private final int quantumSize; // aka. Time Slice
    private int quantumLeft;

    public RoundRobin(int quantumSize){
        if(quantumSize <= 0)
            throw new IllegalArgumentException();
        Processor.LOGGER.info("Quantum size: " + quantumSize);
        this.quantumSize = quantumSize;
    }

    @Override
    public void work() {
        if(runningProc == null){
            // simulating context switch by pulling proc from ready queue
            runningProc = readyQueue.poll();
            if(runningProc == null) return;
            quantumLeft = quantumSize;
            runningProc.setCurrentState(ProcessStates.RUNNING);
            Processor.LOGGER.debug("Running: " + runningProc.toString());
        }

        quantumLeft--;
        runningProc.work();
    }

    @Override
    public void handleNewStates() {
        if(runningProc == null) return;

        boolean canDoContextSwitch = false;

        // The checking order is super important
        if(runningProc.getRequestedState() == ProcessStates.TERM){
            canDoContextSwitch = true;
            Processor.LOGGER.info(runningProc.getName() + " finished at " + (Processor.CURRENT_TICK+1));
        }else if(runningProc.getRequestedState() == ProcessStates.BLOCKING){
            runningProc.setCurrentState(ProcessStates.BLOCKING);
            blockingProc.add(runningProc);
            canDoContextSwitch = true;
        }else if(readyQueue.size() > 0 && quantumLeft <= 0){
            // else "readyQueue is empty", I want -ve quantumLeft value to readily get replaced.
            runningProc.setCurrentState(ProcessStates.READY);
            readyQueue.add(runningProc);
            canDoContextSwitch = true;
        }

        if(canDoContextSwitch){
            // Processor.LOGGER.debug(readyQueue.toString() + blockingProc.toString());
            runningProc = null;
        }
    }
    
}
