package com.vincentcodes.proc;

public interface SchedulePolicy {
    default void submit(SimpleProcess proc) {
        submit(proc, "Proc " + Processor.CURRENT_TICK);
    };
    void submit(SimpleProcess proc, String name);

    // execute the process
    void work();
    void tickBlockingProc();
    void handleNewStates();

    boolean allDone();
}
