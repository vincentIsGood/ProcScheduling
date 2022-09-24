package com.vincentcodes.proc;

import java.util.Objects;
import java.util.function.Consumer;

import com.vincentcodes.logger.Logger;

public class Processor{
    public static int CURRENT_TICK = 0;
    public static final Logger LOGGER = new Logger(){
        {
            // enable(LogType.DEBUG);
        }
    };

    private SchedulePolicy schedulePolicy;

    private int currentTick = 0;
    private Consumer<Integer> handler;

    public Processor(SchedulePolicy schedulePolicy){
        this.schedulePolicy = Objects.requireNonNull(schedulePolicy);
    }

    /**
     * @param handler gives you current tick of the processor
     */
    public void onTick(Consumer<Integer> handler){
        this.handler = handler;
    }

    public void run(){
        while(currentTick == 0 || !schedulePolicy.allDone()){
            if(handler != null)
                handler.accept(currentTick);
            schedulePolicy.work();
            // do this before new proc got added to the blocking list
            schedulePolicy.tickBlockingProc();
            schedulePolicy.handleNewStates();
            tick();
        }
    }

    public void submit(SimpleProcess proc){
        schedulePolicy.submit(proc);
    }
    public void submit(SimpleProcess proc, String name){
        schedulePolicy.submit(proc, name);
    }

    public void tick(){
        CURRENT_TICK = ++currentTick;
    }
}