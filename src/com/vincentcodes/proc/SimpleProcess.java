package com.vincentcodes.proc;

/**
 * arrivalTime has no effect on the processor
 */
public record SimpleProcess(int arrivalTime, int cpuTime, int ioTime, int loop) {
}
