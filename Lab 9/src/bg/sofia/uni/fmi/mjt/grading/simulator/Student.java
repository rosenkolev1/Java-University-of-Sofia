package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.AssignmentType;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.StudentGradingAPI;

import java.util.Arrays;
import java.util.Random;

public class Student implements Runnable {

    private int fn;
    private String name;
    private StudentGradingAPI studentGradingAPI;

    public Student(int fn, String name, StudentGradingAPI studentGradingAPI) {
        this.fn = fn;
        this.name = name;
        this.studentGradingAPI = studentGradingAPI;
    }

    @Override
    public void run() {
        Random random = new Random();
        int assignmentType = random.nextInt(0, AssignmentType.values().length);

        Assignment studentAssignment = new Assignment(this.fn, this.name, AssignmentType.values()[assignmentType]);

        final int maxGradingTime = 1000;
        int randomPauseTime = random.nextInt(0, maxGradingTime + 1);

        try {
            Thread.sleep(randomPauseTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.studentGradingAPI.submitAssignment(studentAssignment);
    }

    public int getFn() {
        return fn;
    }

    public String getName() {
        return name;
    }

    public StudentGradingAPI getGrader() {
        return studentGradingAPI;
    }

}