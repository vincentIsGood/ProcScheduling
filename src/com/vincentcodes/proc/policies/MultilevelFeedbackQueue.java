package com.vincentcodes.proc.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.vincentcodes.proc.ProcessContext;
import com.vincentcodes.proc.ProcessStates;
import com.vincentcodes.proc.Processor;
import com.vincentcodes.proc.SchedulePolicy;
import com.vincentcodes.proc.SimpleProcess;

public class MultilevelFeedbackQueue implements SchedulePolicy {
    // let q0 be the highest priority queue, q3 is the lowest priority queue
    private final List<Queue<ProcessContext>> readyQueues;
    private final List<ProcessContext> blockingProc;
    private final Map<ProcessContext, Integer> processPriority;
    private ProcessContext runningProc;

    private int quantumLeft;

    public MultilevelFeedbackQueue(){
        readyQueues = new ArrayList<>();
        readyQueues.add(new LinkedList<>());
        readyQueues.add(new LinkedList<>());
        readyQueues.add(new LinkedList<>());
        readyQueues.add(new LinkedList<>());
        blockingProc = new ArrayList<>();
        processPriority = new HashMap<>();
    }

    /**
     * Put the proc back to the queue according to its priority.
     */
    private void resubmit(ProcessContext proc){
        submit(proc, processPriority.get(proc));
    }
    /**
     * Demote the proc to lower priority without submitting it back to its queue
     */
    private void pureDemote(ProcessContext proc){
        processPriority.put(proc, Math.min(processPriority.get(proc)+1, 3));
    }
    /**
     * Demote the proc to lower priority and submit it back to its priority queue
     */
    private void demote(ProcessContext proc){
        submit(proc, Math.min(processPriority.get(proc)+1, 3));
    }

    /**
     * @param priority lower level = higher priority
     */
    private void submit(ProcessContext proc, int priority){
        readyQueues.get(priority).add(proc);
        processPriority.put(proc, priority);
    }

    @Override
    public void submit(SimpleProcess proc, String name) {
        submit(new ProcessContext(proc, Processor.CURRENT_TICK, name));
    }
    private void submit(ProcessContext proc){
        submit(proc, 0);
    }

    private ProcessContext pollNextProc(){
        ProcessContext result = null;
        for(Queue<ProcessContext> queue : readyQueues){
            if((result = queue.poll()) != null)
                return result;
        }
        return result;
    }

    @Override
    public void work() {
        if(runningProc == null){
            // simulating context switch by pulling proc from ready queue
            runningProc = pollNextProc();
            if(runningProc == null) return;
            quantumLeft = (int)Math.pow(2, processPriority.get(runningProc)); // quantum = 2**priority
            runningProc.setCurrentState(ProcessStates.RUNNING);
            Processor.LOGGER.debug("Running (w/ quantum "+ quantumLeft +"): " + runningProc.toString());
        }

        quantumLeft--;
        runningProc.work();
        // Processor.LOGGER.debug(runningProc.getName() + " " + quantumLeft);
    }

    @Override
    public void tickBlockingProc(){
        // Processor.LOGGER.debug("---block");
        for(int i = blockingProc.size()-1; i >= 0; i--){
            ProcessContext proc = blockingProc.get(i);
            proc.work();
            if(proc.getCurrentState() == ProcessStates.READY){
                blockingProc.remove(i);
                resubmit(proc);
            }else if(proc.getRequestedState() == ProcessStates.TERM){
                blockingProc.remove(i);
                Processor.LOGGER.info(proc.getName() + " finished at " + (Processor.CURRENT_TICK+1));
            }
        }
        // Processor.LOGGER.debug("---end block");
    }

    @Override
    public void handleNewStates() {
        if(runningProc == null) return;

        // System.out.println(isAllQueuesEmpty());

        boolean canDoContextSwitch = false;

        // The checking order is super important
        if(runningProc.getRequestedState() == ProcessStates.TERM){
            canDoContextSwitch = true;
            Processor.LOGGER.info(runningProc.getName() + " finished at " + (Processor.CURRENT_TICK+1));
        }else if(runningProc.getRequestedState() == ProcessStates.BLOCKING){
            if(quantumLeft <= 0)
                pureDemote(runningProc);
            runningProc.setCurrentState(ProcessStates.BLOCKING);
            blockingProc.add(runningProc);
            canDoContextSwitch = true;
        }else if((!isAllQueuesEmpty() || processPriority.get(runningProc) < 3) && quantumLeft <= 0){
            // else "readyQueue is empty", I want -ve quantumLeft value to readily get replaced.
            // But I still want them to demote until they can't.
            runningProc.setCurrentState(ProcessStates.READY);
            demote(runningProc);
            canDoContextSwitch = true;
        }

        if(canDoContextSwitch){
            // Processor.LOGGER.debug(readyQueue.toString() + blockingProc.toString());
            runningProc = null;
        }
    }

    private boolean isAllQueuesEmpty(){
        return readyQueues.stream().allMatch(queue -> queue.size() == 0);
    }

    @Override
    public boolean allDone() {
        return runningProc == null && isAllQueuesEmpty() && blockingProc.size() == 0;
    }
    
}
