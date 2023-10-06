package com.vincentcodes.proc;

/**
 * arrivalTime has no effect on the processor
 */
public class SimpleProcess {
    public int arrivalTime; 
    public int cpuTime; 
    public int ioTime; 
    public int loop;
    
    public SimpleProcess(int arrivalTime, int cpuTime, int ioTime, int loop) {
        this.arrivalTime = arrivalTime;
        this.cpuTime = cpuTime;
        this.ioTime = ioTime;
        this.loop = loop;
    }

    public int arrivalTime(){
        return arrivalTime;
    }

    public int cpuTime(){
        return cpuTime;
    }

    public int ioTime(){
        return ioTime;
    }

    public int loop(){
        return loop;
    }
}
