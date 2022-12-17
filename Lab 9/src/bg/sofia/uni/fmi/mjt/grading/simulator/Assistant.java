package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.AdminGradingAPI;

import java.util.concurrent.atomic.AtomicInteger;

public class Assistant extends Thread {

    private String name;
    private AdminGradingAPI grader;
    private AtomicInteger gradedAssignmentsCount = new AtomicInteger(0);

    public Assistant(String name, AdminGradingAPI grader) {
        this.name = name;
        this.grader = grader;
    }

    @Override
    public void run() {

        while (true) {
            Assignment ungradedAss = this.grader.getAssignment();

            if (ungradedAss == null) {
                break;
            } else {
                try {
                    Thread.sleep(ungradedAss.type().getGradingTime());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                gradedAssignmentsCount.addAndGet(1);
            }
        }


    }

    public int getNumberOfGradedAssignments() {
        return gradedAssignmentsCount.get();
    }

}