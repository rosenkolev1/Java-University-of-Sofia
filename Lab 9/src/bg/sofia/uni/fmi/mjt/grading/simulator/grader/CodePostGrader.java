package bg.sofia.uni.fmi.mjt.grading.simulator.grader;

import bg.sofia.uni.fmi.mjt.grading.simulator.Assistant;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CodePostGrader implements AdminGradingAPI {

    private List<Assistant> assistants;

    private ConcurrentLinkedQueue<Assignment> assignments;

    private ConcurrentLinkedQueue<Assignment> allSubmittedAssignments;

    private AtomicInteger assignmentsSubmittedCount = new AtomicInteger(0);

    private static boolean finalisedGrading = false;

    public CodePostGrader(int numberOfAssistants) {

        this.assignments = new ConcurrentLinkedQueue<>();
        this.allSubmittedAssignments = new ConcurrentLinkedQueue<>();

        assistants = new ArrayList<>(numberOfAssistants);

        for (int i = 0; i < numberOfAssistants; i++) {
            Assistant newAss = new Assistant("N" + String.valueOf(i), this);
            assistants.add(newAss);
            newAss.start();
        }
    }

    public ConcurrentLinkedQueue<Assignment> getAllSubmittedAssignments() {
        return this.allSubmittedAssignments;
    }

    @Override
    public synchronized Assignment getAssignment() {

        while (!this.finalisedGrading && this.assignments.peek() == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return this.assignments.poll();
    }

    @Override
    public int getSubmittedAssignmentsCount() {
        return assignmentsSubmittedCount.get();
    }

    @Override
    public synchronized void finalizeGrading() {
        this.finalisedGrading = true;
        this.notifyAll();
    }

    @Override
    public List<Assistant> getAssistants() {
        return this.assistants;
    }

    @Override
    public synchronized void submitAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        this.allSubmittedAssignments.add(assignment);
        assignmentsSubmittedCount.addAndGet(1);
        this.notifyAll();
    }
}
