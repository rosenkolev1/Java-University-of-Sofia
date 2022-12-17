import bg.sofia.uni.fmi.mjt.grading.simulator.Student;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.AdminGradingAPI;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.CodePostGrader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {
        CodePostGrader grader = new CodePostGrader(5);

        List<Thread> studentSubmitters = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Student newSubmitter = new Student(i, "Name: " + String.valueOf(i), grader);
            Thread newStudentSubmitter = new Thread(newSubmitter);
            studentSubmitters.add(newStudentSubmitter);
            newStudentSubmitter.start();
        }

        for (var student : studentSubmitters) {
            student.join();
        }

        Thread.sleep(5000);

        System.out.println("Hopefully not deadlocked?");

        grader.finalizeGrading();

        System.out.println("The total submitted assignments: " + grader.getSubmittedAssignmentsCount() + "\n" );

        for(var ass : grader.getAssistants()) {
            System.out.println(ass.getName() + ": " + ass.getNumberOfGradedAssignments());
        }

//        System.out.println(grader.getGradedAssignments());
    }
}