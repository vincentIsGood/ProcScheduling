package com.vincentcodes.proc;

import com.vincentcodes.proc.policies.FirstInFirstOut;
import com.vincentcodes.proc.policies.MultilevelFeedbackQueue;
import com.vincentcodes.proc.policies.RoundRobin;

@SuppressWarnings("unused")
public class Main {
    public static void main(String[] args) {
        // fifoTest();
        // roundRobinTest();
        multilevelFeedbackQueueTest();
    }

    private static void fifoTest(){
        Processor processor = new Processor(new FirstInFirstOut());
        processor.onTick(currentTick ->{
            if(currentTick == 0){
                processor.submit(new SimpleProcess(currentTick, 10, 0, 0), "A");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 2), "B");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 2), "C");
            }
        });
        processor.run();
        System.out.println("Correct answer: A = 10, B = 30, C = 32");
        System.out.println("=========================");

        Processor processor2 = new Processor(new FirstInFirstOut());
        processor2.onTick(currentTick ->{
            if(currentTick == 0){
                processor2.submit(new SimpleProcess(currentTick, 10, 0, 0), "A");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 3), "B");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 3), "C");
            }
        });
        processor2.run();
        System.out.println("Correct answer: A = 10, B = 40, C = 42");
        System.out.println("=========================");

        Processor processor3 = new Processor(new FirstInFirstOut());
        processor3.onTick(currentTick ->{
            if(currentTick == 0){
                processor3.submit(new SimpleProcess(currentTick, 100, 0, 0), "A");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "B");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "C");
            }
        });
        processor3.run();
        System.out.println("Correct answer: A = 100, B = 200, C = 202");
    }

    private static void roundRobinTest(){
        Processor processor = new Processor(new RoundRobin(1));
        processor.onTick(currentTick ->{
            if(currentTick == 0){
                processor.submit(new SimpleProcess(currentTick, 9, 0, 0), "A");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 1), "B");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 1), "C");
            }
        });
        processor.run();
        System.out.println("Correct answer: A = 13, B = 13, C = 14");
        System.out.println("=========================");
        
        Processor processor2 = new Processor(new RoundRobin(1));
        processor2.onTick(currentTick ->{
            if(currentTick == 0){
                processor2.submit(new SimpleProcess(currentTick, 17, 0, 0), "A");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 2), "B");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 2), "C");
            }
        });
        processor2.run();
        System.out.println("Correct answer: A = 25, B = 25, C = 27");
        System.out.println("=========================");

        Processor processor3 = new Processor(new RoundRobin(1));
        processor3.onTick(currentTick ->{
            if(currentTick == 0){
                processor3.submit(new SimpleProcess(currentTick, 100, 0, 0), "A");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "B");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "C");
            }
        });
        processor3.run();
        System.out.println("Correct answer: A = 140, B = 113, C = 117");
        System.out.println("=========================");
        
        Processor processor4 = new Processor(new RoundRobin(100));
        processor4.onTick(currentTick ->{
            if(currentTick == 0){
                processor4.submit(new SimpleProcess(currentTick, 100, 0, 0), "A");
                processor4.submit(new SimpleProcess(currentTick, 2, 8, 10), "B");
                processor4.submit(new SimpleProcess(currentTick, 2, 8, 10), "C");
            }
        });
        processor4.run();
        System.out.println("Correct answer: A = 100, B = 200, C = 202");
        System.out.println("=========================");
    }

    private static void multilevelFeedbackQueueTest() {
        Processor processor = new Processor(new MultilevelFeedbackQueue());
        processor.onTick(currentTick ->{
            if(currentTick == 0){
                processor.submit(new SimpleProcess(currentTick, 23, 0, 0), "A");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 2), "B");
                processor.submit(new SimpleProcess(currentTick, 2, 8, 2), "C");
            }
        });
        processor.run();
        System.out.println("Correct answer: A = 31, B = 29, C = 31");
        System.out.println("=========================");

        Processor processor2 = new Processor(new MultilevelFeedbackQueue());
        processor2.onTick(currentTick ->{
            if(currentTick == 0){
                processor2.submit(new SimpleProcess(currentTick, 31, 0, 0), "A");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 3), "B");
                processor2.submit(new SimpleProcess(currentTick, 2, 8, 3), "C");
            }
        });
        processor2.run();
        System.out.println("Correct answer: A = 43, B = 41, C = 43");
        System.out.println("=========================");

        Processor processor3 = new Processor(new MultilevelFeedbackQueue());
        processor3.onTick(currentTick ->{
            if(currentTick == 0){
                processor3.submit(new SimpleProcess(currentTick, 100, 0, 0), "A");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "B");
                processor3.submit(new SimpleProcess(currentTick, 2, 8, 10), "C");
            }
        });
        processor3.run();
        System.out.println("Correct answer: A = 43, B = 41, C = 43");
        System.out.println("=========================");
    }
}
