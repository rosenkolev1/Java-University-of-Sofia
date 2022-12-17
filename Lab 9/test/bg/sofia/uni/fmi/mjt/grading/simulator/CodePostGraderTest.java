package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.Student;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.AdminGradingAPI;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.CodePostGrader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CodePostGraderTest {

    @Test
    public void testCodePostGrader() throws InterruptedException {
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

        grader.finalizeGrading();

        int totalGradedAssignments = 0;

        for(var ass : grader.getAssistants()) {
            totalGradedAssignments += ass.getNumberOfGradedAssignments();
        }

        int expectedTotalSubmittedAndGradedAssignments = 30;

        int totalSubmittedAssignments = grader.getSubmittedAssignmentsCount();

        Assertions.assertEquals(expectedTotalSubmittedAndGradedAssignments,
            totalSubmittedAssignments,
            "The total submitted assignments count is incorrect!");

        Assertions.assertEquals(expectedTotalSubmittedAndGradedAssignments,
            totalGradedAssignments,
            "The total graded assignments count is incorrect!");

        Assertions.assertEquals(expectedTotalSubmittedAndGradedAssignments,
            grader.getAllSubmittedAssignments().size(),
            "The total submitted assignments count is incorrect!");
    }
}
