package com.vincentcodes.proc;

public class ProcessContext {
    private SimpleProcess proc;
    private int id;
    private String name;

    private ProcessStates currentState = ProcessStates.READY;
    private ProcessStates requestState = null;
    private int timeLeft;
    private int loopLeft;

    // Search "assumed behavior" 
    // Behavior: must switch to IO (blocking) then normal alternatively.
    private boolean isPreviousBlocking = false;

    public ProcessContext(SimpleProcess proc, int id, String name){
        this.proc = proc;
        this.id = id;
        this.name = name;
        timeLeft = proc.cpuTime();
        loopLeft = proc.loop();
    }

    /**
     * Decrease the timeLeft by 1 ms
     */
    public void work(){
        if(currentState == ProcessStates.BLOCKING){
            handleBlocking();
            return;
        }
        timeLeft--;

        if(timeLeft <= 0){
            updateState();
        }
    }
    public void handleBlocking(){
        timeLeft--;
        // Processor.LOGGER.debug(name + " " + timeLeft);

        if(timeLeft <= 0){
            isPreviousBlocking = true;
            updateState();
        }
    }
    private void updateState(){
        if(loopLeft == 0){
            requestState = ProcessStates.TERM;
            return;
        }
        // assumed behavior
        if(!isPreviousBlocking && proc.ioTime() > 0){
            block();
            loopLeft--;
        }else{
            ready();
        }
        Processor.LOGGER.debug(name + " is done, new time: " + timeLeft + ", req state: " + requestState);
    }
    private void block(){
        requestState = ProcessStates.BLOCKING;
        timeLeft = proc.ioTime();
        isPreviousBlocking = true; // assumed behavior
    }
    private void ready(){
        currentState = ProcessStates.READY;
        timeLeft = proc.cpuTime();
        isPreviousBlocking = false; // assumed behavior
    }

    public int id(){
        return id;
    }

    public String getName(){
        return name;
    }
    
    public ProcessStates getCurrentState(){
        return currentState;
    }
    public ProcessStates getRequestedState(){
        return requestState;
    }
    public void setCurrentState(ProcessStates state){
        currentState = state;
        requestState = null;
    }

    public String toString(){
        return String.format("{id: %d, name: '%s', timeLeft: %d, state: %s}", id, name, timeLeft, currentState);
    }
}
